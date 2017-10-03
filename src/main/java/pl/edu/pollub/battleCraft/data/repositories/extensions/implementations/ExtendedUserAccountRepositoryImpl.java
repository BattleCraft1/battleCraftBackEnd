package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedUserAccountRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.UserAccountRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExtendedUserAccountRepositoryImpl implements ExtendedUserAccountRepository {
    private final GetPageAssistant getPageAssistant;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organiserRepository;

    @Autowired
    public ExtendedUserAccountRepositoryImpl(GetPageAssistant getPageAssistant,
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
                        new Field("province.location", "province"),
                        new Field("status", "status"),
                        new Field("banned", "banned")
                )
                .join(
                        new Join("address", "address"),
                        new Join("address.province", "province")
                )
                .from(UserAccount.class)
                .where(searchCriteria)
                .groupBy("id", "banned", "address.city", "province.location")
                .execute(requestedPage);
    }

    @Override
    public void banUsersAccounts(String... usersAccountsToBanUniqueNames) {
        playerRepository.banUserAccounts(usersAccountsToBanUniqueNames);
    }


    @Override
    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccounts(usersAccountsToUnlockUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersAccountsToAcceptUniqueNames) {
        List<UserAccount> userAccountsToAccept =
                userAccountRepository.findAllUsersAccountsByName(usersAccountsToAcceptUniqueNames);
        userAccountRepository.deleteRelatedAddress(usersAccountsToAcceptUniqueNames);
        userAccountRepository.delete(userAccountsToAccept);
        List<Player> acceptedUserAccounts = this.advanceUsersToPlayers(userAccountsToAccept);
        playerRepository.save(acceptedUserAccounts);
    }


    @Override
    public void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames) {
        playerRepository.deleteParticipationInTournaments(usersAccountsToDeleteUniqueNames);
        playerRepository.deletePlayInTournaments(usersAccountsToDeleteUniqueNames);
        organiserRepository.deleteOrganizationOfTournaments(usersAccountsToDeleteUniqueNames);
        organiserRepository.deleteCreationOfGames(usersAccountsToDeleteUniqueNames);
        userAccountRepository.deleteRelatedAddress(usersAccountsToDeleteUniqueNames);
        userAccountRepository.deleteUserAccounts(usersAccountsToDeleteUniqueNames);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames) {
        playerRepository.cancelAcceptUsersAccounts(usersAccountsToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<Player> playersToAdvance =
                playerRepository.findAllPlayersByName(playersToAdvanceToOrganizersUniqueNames);
        playerRepository.deleteParticipationInTournaments(playersToAdvanceToOrganizersUniqueNames);
        userAccountRepository.deleteRelatedAddress(playersToAdvanceToOrganizersUniqueNames);
        playerRepository.delete(playersToAdvance);
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        organiserRepository.save(organizers);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        organiserRepository.degradeOrganizersToPlayers(organizerToDegradeToPlayersUniqueNames);
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
