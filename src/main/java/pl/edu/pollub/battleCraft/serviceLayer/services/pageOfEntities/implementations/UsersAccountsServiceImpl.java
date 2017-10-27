package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.UsersAccountsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.validators.interfaces.UniqueNamesValidator;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.UsersAccountsService;

import java.util.List;

@Service
public class UsersAccountsServiceImpl implements UsersAccountsService {

    private final UsersAccountsRepository userAccountsRepository;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organizerRepository;
    private final UniqueNamesValidator uniqueNamesValidator;

    @Autowired
    public UsersAccountsServiceImpl(UsersAccountsRepository usersAccountsRepository, UserAccountRepository userAccountRepository, PlayerRepository playerRepository, OrganizerRepository organizerRepository, UniqueNamesValidator uniqueNamesValidator) {
        this.userAccountsRepository = usersAccountsRepository;
        this.userAccountRepository = userAccountRepository;
        this.playerRepository = playerRepository;
        this.organizerRepository = organizerRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
    }

    @Override
    public Page getPageOfUserAccounts(Pageable requestedPage,
                                      List<SearchCriteria> searchCriteria) {
        return userAccountsRepository.getPageOfUserAccounts(searchCriteria, requestedPage);
    }

    @Override
    public void banUsersAccounts(String... usersToBanUniqueNames) {
        userAccountsRepository.banUsersAccounts(usersToBanUniqueNames);
    }

    @Override
    public void unlockUsersAccounts(String... usersToUnlockUniqueNames) {
        userAccountsRepository.unlockUsersAccounts(usersToUnlockUniqueNames);
    }

    @Override
    public void deleteUsersAccounts(String... usersToDeleteUniqueNames){
        List<String> validUniqueNames = userAccountRepository.selectUsersAccountsToDeleteUniqueNames(usersToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,usersToDeleteUniqueNames);

        userAccountsRepository.deleteUsersAccounts(usersToDeleteUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersToAcceptUniqueNames) {
        List<String> validUniqueNames = userAccountRepository.selectUsersAccountsToAcceptUniqueNames(usersToAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,usersToAcceptUniqueNames);

        userAccountsRepository.acceptUsersAccounts(usersToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames) {
        List<String> validUniqueNames = playerRepository.selectUsersAccountsToRejectUniqueNames(usersToCancelAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToReject(validUniqueNames,usersToCancelAcceptUniqueNames);

        userAccountsRepository.cancelAcceptUsersAccounts(usersToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<String> validUniqueNames = playerRepository.selectUsersAccountsToAdvanceUniqueNames(playersToAdvanceToOrganizersUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAdvance(validUniqueNames,playersToAdvanceToOrganizersUniqueNames);

        userAccountsRepository.advancePlayersToOrganizer(playersToAdvanceToOrganizersUniqueNames);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        List<String> validUniqueNames = organizerRepository.selectUsersAccountsToDegadeUniqueNames(organizerToDegradeToPlayersUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDegrade(validUniqueNames,organizerToDegradeToPlayersUniqueNames);

        userAccountsRepository.degradeOrganizerToPlayers(organizerToDegradeToPlayersUniqueNames);
    }
}
