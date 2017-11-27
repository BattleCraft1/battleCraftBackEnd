package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers.PlayerToOrganizerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.mappers.UserAccountToPlayerMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsersAccountsRepository{
    private final Searcher searcher;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organiserRepository;
    private final PlayerToOrganizerMapper playerToOrganizerMapper;
    private final UserAccountToPlayerMapper userAccountToPlayerMapper;

    @Autowired
    public UsersAccountsRepository(Searcher searcher,
                                   UserAccountRepository userAccountRepository,
                                   PlayerRepository playerRepository,
                                   OrganizerRepository organiserRepository, PlayerToOrganizerMapper playerToOrganizerMapper, UserAccountToPlayerMapper userAccountToPlayerMapper) {
        this.searcher = searcher;
        this.playerRepository = playerRepository;
        this.organiserRepository = organiserRepository;
        this.userAccountRepository = userAccountRepository;
        this.playerToOrganizerMapper = playerToOrganizerMapper;
        this.userAccountToPlayerMapper = userAccountToPlayerMapper;
    }

    @Transactional
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

    public void banUsersAccounts(List<Player> players) {

        this.removeParticipationOfPlayers(players);

        List<Organizer> organizers = this.castPlayersToOrganizers(players);

        this.removeOrganizationsOfOrganizers(organizers);

        players.forEach(player -> player.setBanned(true));

        playerRepository.save(players);
    }

    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccountsByUniqueNames(usersAccountsToUnlockUniqueNames);
    }

    public void acceptUsersAccounts(List<UserAccount> userAccountsToAccept) {
        userAccountRepository.delete(userAccountsToAccept);
        List<Player> acceptedUserAccounts = this.advanceUsersToPlayers(userAccountsToAccept);
        playerRepository.save(acceptedUserAccounts);
    }

    public void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames) {
        List<Long> idsUsersToDelete = userAccountRepository.selectIdsOfUsersToDelete(usersAccountsToDeleteUniqueNames);
        playerRepository.deleteParticipationByPlayersIds(idsUsersToDelete);
        playerRepository.deletePlayByPlayersIds(idsUsersToDelete);
        organiserRepository.deleteOrganizationByIds(idsUsersToDelete);
        organiserRepository.deleteCreationOfGamesByOrganizersIds(idsUsersToDelete);
        userAccountRepository.deleteUsersAccountsByIds(idsUsersToDelete);
        userAccountRepository.deleteRelatedAddress(idsUsersToDelete);
    }

    public void cancelAcceptUsersAccounts(List<Player> players) {

        this.removeParticipationOfPlayers(players);

        List<Organizer> organizers = this.castPlayersToOrganizers(players);

        this.removeOrganizationsOfOrganizers(organizers);

        players.forEach(player -> player.setStatus(UserType.NEW));

        playerRepository.save(players);
    }

    public void advancePlayersToOrganizer(List<Player> playersToAdvance) {
        playerRepository.delete(playersToAdvance);
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        organiserRepository.save(organizers);
    }

    public void degradeOrganizerToPlayers(List<Organizer> organizers) {

        this.removeOrganizationsOfOrganizers(organizers);

        organizers.forEach(organizer -> organizer.setStatus(UserType.ACCEPTED));

        organiserRepository.save(organizers);
    }

    private List<Player> advanceUsersToPlayers(List<UserAccount> userAccounts){
        List<Player> players = new ArrayList<>();
        userAccounts.forEach(
                userAccount -> {
                    if(userAccount.getStatus()==UserType.NEW)
                        players.add(userAccountToPlayerMapper.map(userAccount));
                }
        );
        return players;
    }

    private List<Organizer> advancePlayersToOrganizers(List<Player> players){
        List<Organizer> organizers = new ArrayList<>();
        players.forEach(
                player -> {
                    if(player.getStatus()==UserType.ACCEPTED)
                        organizers.add(playerToOrganizerMapper.map(player));
                }
        );
        return organizers;
    }

    private Join[] generateJoins(List<SearchCriteria> searchCriteria){
        SearchCriteria ifParticipateSearchCriteria = searchCriteria.stream()
                .filter(searchCriteria1 -> searchCriteria1.getOperation().equalsIgnoreCase("not participate"))
                .findFirst().orElse(null);

        if(ifParticipateSearchCriteria!=null){
            return  new Join[]{new Join("address", "address"),
                    new Join("participation", "participation")};
        }
        else{
            return new Join[]{new Join("address", "address")};
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
