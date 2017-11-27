package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers.PlayerToOrganizerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.mappers.UserAccountToPlayerMapper;

import java.util.ArrayList;
import java.util.List;

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

    public void banUsersAccounts(String... usersAccountsToBanUniqueNames) {
        playerRepository.banUserAccountsByUniqueNames(usersAccountsToBanUniqueNames);
    }

    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccountsByUniqueNames(usersAccountsToUnlockUniqueNames);
    }

    public void acceptUsersAccounts(String... usersAccountsToAcceptUniqueNames) {
        List<UserAccount> userAccountsToAccept = userAccountRepository.findAllUsersAccountsByUniqueName(usersAccountsToAcceptUniqueNames);
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

    public void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames) {
        playerRepository.cancelAcceptationOfUsersAccountsByUniqueNames(usersAccountsToCancelAcceptUniqueNames);
    }

    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<Player> playersToAdvance = playerRepository.findPlayersByUniqueName(playersToAdvanceToOrganizersUniqueNames);
        playerRepository.delete(playersToAdvance);
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        organiserRepository.save(organizers);
    }

    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        organiserRepository.degradeOrganizersToPlayersByPlayersUniqueNames(organizerToDegradeToPlayersUniqueNames);
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
}
