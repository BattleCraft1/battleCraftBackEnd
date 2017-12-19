package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.PageNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsersAccountsRepository{

    @PersistenceContext
    private EntityManager entityManager;
    private final Searcher searcher;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organiserRepository;

    @Autowired
    public UsersAccountsRepository(Searcher searcher,
                                   UserAccountRepository userAccountRepository,
                                   PlayerRepository playerRepository,
                                   OrganizerRepository organiserRepository) {
        this.searcher = searcher;
        this.playerRepository = playerRepository;
        this.organiserRepository = organiserRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(rollbackFor = {AnyObjectNotFoundException.class,PageNotFoundException.class})
    public Page getPageOfUserAccounts(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return searcher
                .select(
                        new Field("firstname", "firstname"),
                        new Field("lastname", "lastname"),
                        new Field("name", "name"),
                        new Field("email", "email"),
                        new Field("phoneNumber", "phoneNumber"),
                        new Field("address.city", "city"),
                        new Field("address.province", "province"),
                        new Field("status", "status"),
                        new Field("banned", "banned")
                )
                .join(
                        this.generateJoins(searchCriteria)
                )
                .from(UserAccount.class)
                .where(searchCriteria)
                .groupBy("id","banned", "address.city", "address.province")
                .execute("id",requestedPage);
    }

    @Transactional
    public void banUsersAccounts(List<Player> players) {

        this.removeParticipationOfPlayers(players);

        List<Organizer> organizers = this.castPlayersToOrganizers(players);

        this.removeOrganizationsOfOrganizers(organizers);

        players.forEach(player -> player.setBanned(true));

        playerRepository.save(players);
    }

    @Transactional
    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccountsByUniqueNames(usersAccountsToUnlockUniqueNames);
    }

    @Transactional
    public void acceptUsersAccounts(List<UserAccount> userAccountsToAccept) {
        List<String> namesOfUserAccountsToAdvanceFirstTime = new ArrayList<>();
        List<Player> userAccountsToAdvanceNextTime = new ArrayList<>();
        userAccountsToAccept.forEach(
                userAccount -> {
                    if(userAccount instanceof Player) {
                        userAccountsToAdvanceNextTime.add((Player)userAccount);

                    }
                    else{
                        userAccount.setStatus(UserType.ACCEPTED);
                        namesOfUserAccountsToAdvanceFirstTime.add(userAccount.getName());
                    }
                });
        entityManager.createNativeQuery("UPDATE user_account SET status = 'ACCEPTED', role = 'Player' WHERE name in (:uniqueNames)")
                .setParameter("uniqueNames",namesOfUserAccountsToAdvanceFirstTime).executeUpdate();
        playerRepository.save(userAccountsToAdvanceNextTime);
    }

    @Transactional
    public void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames) {
        List<Long> idsUsersToDelete = userAccountRepository.selectIdsOfUsersToDelete(usersAccountsToDeleteUniqueNames);
        playerRepository.deleteParticipationByPlayersIds(idsUsersToDelete);
        playerRepository.deletePlayByPlayersIds(idsUsersToDelete);
        organiserRepository.deleteOrganizationByIds(idsUsersToDelete);
        organiserRepository.deleteCreationOfGamesByOrganizersIds(idsUsersToDelete);
        userAccountRepository.deleteUsersAccountsByIds(idsUsersToDelete);
        userAccountRepository.deleteRelatedAddresses(idsUsersToDelete);
    }

    @Transactional
    public void cancelAcceptUsersAccounts(List<Player> players) {

        this.removeParticipationOfPlayers(players);

        List<Organizer> organizers = this.castPlayersToOrganizers(players);

        this.removeOrganizationsOfOrganizers(organizers);

        players.forEach(player -> player.setStatus(UserType.NEW));

        playerRepository.save(players);
    }

    @Transactional
    public void advancePlayersToOrganizer(List<Player> playersToAdvance) {
        List<String> namesOfUserAccountsToAdvanceFirstTime = new ArrayList<>();
        List<Organizer> userAccountsToAdvanceNextTime = new ArrayList<>();
        playersToAdvance.forEach(
                player -> {
                    if(player instanceof Organizer) {
                        userAccountsToAdvanceNextTime.add((Organizer)player);
                    }
                    else{
                        player.setStatus(UserType.ORGANIZER);
                        namesOfUserAccountsToAdvanceFirstTime.add(player.getName());
                    }
                }
        );
        entityManager.createNativeQuery("UPDATE user_account SET status = 'ORGANIZER', role = 'Organizer' WHERE name in (:uniqueNames)")
                .setParameter("uniqueNames",namesOfUserAccountsToAdvanceFirstTime).executeUpdate();
        organiserRepository.save(userAccountsToAdvanceNextTime);
    }

    @Transactional
    public void degradeOrganizerToPlayers(List<Organizer> organizers) {

        this.removeOrganizationsOfOrganizers(organizers);

        organizers.forEach(organizer -> organizer.setStatus(UserType.ACCEPTED));

        organiserRepository.save(organizers);
    }

    private Join[] generateJoins(List<SearchCriteria> searchCriteria){
        SearchCriteria ifParticipateSearchCriteria = searchCriteria.stream()
                .filter(searchCriteria1 -> searchCriteria1.getOperation().equalsIgnoreCase("not participate"))
                .findFirst().orElse(null);

        if(ifParticipateSearchCriteria!=null){
            return  new Join[]{
                    new Join("addressOwnership.address", "address"),
                    new Join("participation", "participation")};
        }
        else{
            return new Join[]{
                    new Join("addressOwnership.address", "address")};
        }
    }

    private void removeParticipationOfPlayers(List<Player> players){
        players.forEach(
                player -> {
                    List<Participation> participationToDelete = player.getParticipation().stream()
                            .filter(participation -> {
                                TournamentStatus status = participation.getParticipatedTournament().getStatus();
                                return status==TournamentStatus.ACCEPTED || status==TournamentStatus.NEW;
                            }).collect(Collectors.toList());

                    participationToDelete.forEach(
                            participation -> {
                                Tournament tournament = participation.getParticipatedTournament();
                                tournament.deleteParticipationByOneSide(participation);
                                participation.setPlayer(null);
                                participation.setParticipatedTournament(null);
                            }
                    );

                    player.getParticipation().removeAll(participationToDelete);
                }
        );
    }

    private void removeOrganizationsOfOrganizers(List<Organizer> organizers){
        organizers.forEach(
                organizer -> {
                    List<Organization> organizationToDelete = organizer.getOrganizations().stream()
                            .filter(organization -> {
                                TournamentStatus status = organization.getOrganizedTournament().getStatus();
                                return status==TournamentStatus.ACCEPTED || status==TournamentStatus.NEW;
                            }).collect(Collectors.toList());

                    organizationToDelete.forEach(
                            organization -> {
                                Tournament tournament = organization.getOrganizedTournament();
                                tournament.deleteOrganizationByOneSide(organization);
                                organization.setOrganizer(null);
                                organization.setOrganizedTournament(null);
                            }
                    );

                    organizer.getOrganizations().removeAll(organizationToDelete);
                }
        );
    }

    private List<Organizer> castPlayersToOrganizers(List<Player> players){
        return players.stream()
                .filter(player -> player instanceof Organizer)
                .map(player -> ((Organizer)player))
                .collect(Collectors.toList());
    }
}
