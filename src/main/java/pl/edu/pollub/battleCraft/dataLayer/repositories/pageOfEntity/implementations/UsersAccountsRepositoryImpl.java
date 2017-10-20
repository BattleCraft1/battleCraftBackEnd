package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.UsersAccountsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces.SearchAssistant;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsersAccountsRepositoryImpl implements UsersAccountsRepository {
    private final SearchAssistant getPageAssistant;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organiserRepository;

    @Autowired
    public UsersAccountsRepositoryImpl(SearchAssistant getPageAssistant,
                                       UserAccountRepository userAccountRepository,
                                       PlayerRepository playerRepository,
                                       OrganizerRepository organiserRepository) {
        this.getPageAssistant = getPageAssistant;
        this.playerRepository = playerRepository;
        this.organiserRepository = organiserRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public Page getPageOfUserAccounts(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return getPageAssistant
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
                        new Join("address", "address")
                )
                .from(UserAccount.class)
                .where(searchCriteria)
                .groupBy("id", "banned", "address.city", "address.province")
                .execute("id",requestedPage);
    }

    @Override
    public void banUsersAccounts(String... usersAccountsToBanUniqueNames) {
        playerRepository.banUserAccountsByUniqueNames(usersAccountsToBanUniqueNames);
    }


    @Override
    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccountsByUniqueNames(usersAccountsToUnlockUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersAccountsToAcceptUniqueNames) {
        List<UserAccount> userAccountsToAccept =
                userAccountRepository.findAllUsersAccountsByUniqueName(usersAccountsToAcceptUniqueNames);
        userAccountRepository.delete(userAccountsToAccept);
        List<Player> acceptedUserAccounts = this.advanceUsersToPlayers(userAccountsToAccept);
        playerRepository.save(acceptedUserAccounts);
    }


    @Override
    public void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames) {
        List<Long> idsUsersToDelete = userAccountRepository.selectIdsOfUsersToDelete(usersAccountsToDeleteUniqueNames);
        playerRepository.deleteParticipationByPlayersIds(idsUsersToDelete);
        playerRepository.deletePlayByPlayersIds(idsUsersToDelete);
        organiserRepository.deleteOrganizationByIds(idsUsersToDelete);
        organiserRepository.deleteCreationOfGamesByOrganizersIds(idsUsersToDelete);
        userAccountRepository.deleteUsersAccountsByIds(idsUsersToDelete);
        userAccountRepository.deleteRelatedAddress(idsUsersToDelete);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames) {
        playerRepository.cancelAcceptationOfUsersAccountsByUniqueNames(usersAccountsToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<Player> playersToAdvance = playerRepository.findPlayersByUniqueName(playersToAdvanceToOrganizersUniqueNames);
        playerRepository.delete(playersToAdvance);
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        organiserRepository.save(organizers);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        organiserRepository.degradeOrganizersToPlayersByPlayersUniqueNames(organizerToDegradeToPlayersUniqueNames);
    }

    private List<Player> advanceUsersToPlayers(List<UserAccount> userAccounts){
        List<Player> players = new ArrayList<>();
        userAccounts.forEach(
                userAccount -> {
                    if(userAccount.getStatus()==UserType.NEW)
                        players.add(new Player(userAccount));
                }
        );
        return players;
    }

    private List<Organizer> advancePlayersToOrganizers(List<Player> players){
        List<Organizer> organizers = new ArrayList<>();
        players.forEach(
                player -> {
                    if(player.getStatus()==UserType.ACCEPTED)
                        organizers.add(new Organizer(player));
                }
        );
        return organizers;
    }
}
