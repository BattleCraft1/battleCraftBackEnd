package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.UsersAccountsRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.UserAccountResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersAccountsService {

    private final UsersAccountsRepository userAccountsRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountResourcesService userAccountResourcesService;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organizerRepository;
    private final UniqueNamesValidator uniqueNamesValidator;
    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public UsersAccountsService(UsersAccountsRepository usersAccountsRepository, UserAccountRepository userAccountRepository, UserAccountResourcesService userAccountResourcesService, PlayerRepository playerRepository, OrganizerRepository organizerRepository, UniqueNamesValidator uniqueNamesValidator, AuthorityRecognizer authorityRecognizer) {
        this.userAccountsRepository = usersAccountsRepository;
        this.userAccountRepository = userAccountRepository;
        this.userAccountResourcesService = userAccountResourcesService;
        this.playerRepository = playerRepository;
        this.organizerRepository = organizerRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
        this.authorityRecognizer = authorityRecognizer;
    }

    public Page getPageOfUserAccounts(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        authorityRecognizer.modifyUsersSearchCriteriaForCurrentUser(searchCriteria);
        return userAccountsRepository.getPageOfUserAccounts(searchCriteria, requestedPage);
    }

    public void banUsersAccounts(String... usersToBanUniqueNames) {
        List<Player> usersToBan = playerRepository.findNotBannedPlayersByUniqueName(usersToBanUniqueNames);
        userAccountsRepository.banUsersAccounts(usersToBan);
    }

    public void unlockUsersAccounts(String... usersToUnlockUniqueNames) {
        userAccountsRepository.unlockUsersAccounts(usersToUnlockUniqueNames);
    }

    public void deleteUsersAccounts(String... usersToDeleteUniqueNames){
        List<UserAccount> validObjects = userAccountRepository.selectUsersAccountsToDeleteByUniqueNames(usersToDeleteUniqueNames);

        List<String> uniqueNamesOfUsersWhoHaveTournamentsInProgress = new ArrayList<>();

        validObjects.forEach(
                userAccount -> {
                    if(userAccount instanceof Player){
                        Player player = (Player) userAccount;
                        player.getParticipation().stream().map(Participation::getParticipatedTournament)
                                .filter(tournament -> tournament.getStatus() == TournamentStatus.IN_PROGRESS)
                                .findAny().ifPresent(tournament -> uniqueNamesOfUsersWhoHaveTournamentsInProgress.add(player.getName()));
                    }

                    if(userAccount instanceof Organizer){
                        Organizer organizer = (Organizer) userAccount;
                        organizer.getOrganizations().stream().map(Organization::getOrganizedTournament)
                                .filter(tournament -> tournament.getStatus() == TournamentStatus.IN_PROGRESS)
                                .findAny().ifPresent(tournament -> uniqueNamesOfUsersWhoHaveTournamentsInProgress.add(organizer.getName()));
                    }

                    if(userAccount instanceof Administrator){
                        uniqueNamesOfUsersWhoHaveTournamentsInProgress.add(userAccount.getName());
                    }

                }
        );

        List<String> validUniqueNames = validObjects.stream().map(UserAccount::getName).collect(Collectors.toList());
        validUniqueNames.removeAll(uniqueNamesOfUsersWhoHaveTournamentsInProgress);
        uniqueNamesValidator.validateUniqueNamesOfUsersToDelete(validUniqueNames,usersToDeleteUniqueNames);

        try {
            userAccountResourcesService.deleteUsersAccountsAvatars(validUniqueNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userAccountsRepository.deleteUsersAccounts(usersToDeleteUniqueNames);
    }

    public void acceptUsersAccounts(String... usersToAcceptUniqueNames) {
        List<UserAccount> validObjects = userAccountRepository.selectUsersAccountsToAcceptByUniqueNames(usersToAcceptUniqueNames);

        List<String> validUniqueNames = validObjects.stream().map(UserAccount::getName).collect(Collectors.toList());
        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,usersToAcceptUniqueNames);

        userAccountsRepository.acceptUsersAccounts(validObjects);
    }

    public void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames) {
        List<Player> validObjects = playerRepository.selectUsersAccountsToRejectByUniqueNames(usersToCancelAcceptUniqueNames);

        List<String> validUniqueNames = validObjects.stream().map(UserAccount::getName).collect(Collectors.toList());
        uniqueNamesValidator.validateUniqueNamesElementsToReject(validUniqueNames,usersToCancelAcceptUniqueNames);

        userAccountsRepository.cancelAcceptUsersAccounts(validObjects);
    }

    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<Player> validObjects = playerRepository.selectUsersAccountsToAdvanceByUniqueNames(playersToAdvanceToOrganizersUniqueNames);

        List<String> validUniqueNames = validObjects.stream().map(UserAccount::getName).collect(Collectors.toList());
        uniqueNamesValidator.validateUniqueNamesElementsToAdvance(validUniqueNames,playersToAdvanceToOrganizersUniqueNames);

        userAccountsRepository.advancePlayersToOrganizer(validObjects);
    }

    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        List<Organizer> validObjects = organizerRepository.selectUsersAccountsToDegradeByUniqueNames(organizerToDegradeToPlayersUniqueNames);

        List<String> validUniqueNames = validObjects.stream().map(UserAccount::getName).collect(Collectors.toList());
        uniqueNamesValidator.validateUniqueNamesElementsToDegrade(validUniqueNames,organizerToDegradeToPlayersUniqueNames);

        userAccountsRepository.degradeOrganizerToPlayers(validObjects);
    }
}
