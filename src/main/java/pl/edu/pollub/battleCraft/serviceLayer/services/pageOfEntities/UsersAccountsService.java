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
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import java.util.Collections;
import java.util.List;

@Service
public class UsersAccountsService {

    private final UsersAccountsRepository userAccountsRepository;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organizerRepository;
    private final UniqueNamesValidator uniqueNamesValidator;
    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public UsersAccountsService(UsersAccountsRepository usersAccountsRepository, UserAccountRepository userAccountRepository, PlayerRepository playerRepository, OrganizerRepository organizerRepository, UniqueNamesValidator uniqueNamesValidator, AuthorityRecognizer authorityRecognizer) {
        this.userAccountsRepository = usersAccountsRepository;
        this.userAccountRepository = userAccountRepository;
        this.playerRepository = playerRepository;
        this.organizerRepository = organizerRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
        this.authorityRecognizer = authorityRecognizer;
    }

    public Page getPageOfUserAccounts(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        authorityRecognizer.modifySearchCriteriaForCurrentUserRole(searchCriteria);
        return userAccountsRepository.getPageOfUserAccounts(searchCriteria, requestedPage);
    }

    public void banUsersAccounts(String... usersToBanUniqueNames) {
        userAccountsRepository.banUsersAccounts(usersToBanUniqueNames);
    }

    public void unlockUsersAccounts(String... usersToUnlockUniqueNames) {
        userAccountsRepository.unlockUsersAccounts(usersToUnlockUniqueNames);
    }

    public void deleteUsersAccounts(String... usersToDeleteUniqueNames){
        List<String> validUniqueNames = userAccountRepository.selectUsersAccountsToDeleteUniqueNames(usersToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,usersToDeleteUniqueNames);

        userAccountsRepository.deleteUsersAccounts(usersToDeleteUniqueNames);
    }

    public void acceptUsersAccounts(String... usersToAcceptUniqueNames) {
        List<String> validUniqueNames = userAccountRepository.selectUsersAccountsToAcceptUniqueNames(usersToAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,usersToAcceptUniqueNames);

        userAccountsRepository.acceptUsersAccounts(usersToAcceptUniqueNames);
    }

    public void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames) {
        List<String> validUniqueNames = playerRepository.selectUsersAccountsToRejectUniqueNames(usersToCancelAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToReject(validUniqueNames,usersToCancelAcceptUniqueNames);

        userAccountsRepository.cancelAcceptUsersAccounts(usersToCancelAcceptUniqueNames);
    }

    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<String> validUniqueNames = playerRepository.selectUsersAccountsToAdvanceUniqueNames(playersToAdvanceToOrganizersUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAdvance(validUniqueNames,playersToAdvanceToOrganizersUniqueNames);

        userAccountsRepository.advancePlayersToOrganizer(playersToAdvanceToOrganizersUniqueNames);
    }

    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        List<String> validUniqueNames = organizerRepository.selectUsersAccountsToDegadeUniqueNames(organizerToDegradeToPlayersUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDegrade(validUniqueNames,organizerToDegradeToPlayersUniqueNames);

        userAccountsRepository.degradeOrganizerToPlayers(organizerToDegradeToPlayersUniqueNames);
    }
}
