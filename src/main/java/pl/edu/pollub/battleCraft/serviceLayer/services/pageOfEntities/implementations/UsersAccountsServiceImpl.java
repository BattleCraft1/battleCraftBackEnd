package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.UsersAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.UsersAccountsService;

import java.util.List;

@Service
public class UsersAccountsServiceImpl implements UsersAccountsService {

    private final UsersAccountRepository userAccountRepository;

    @Autowired
    public UsersAccountsServiceImpl(UsersAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Page getPageOfUserAccounts(Pageable requestedPage,
                                      List<SearchCriteria> searchCriteria) {
        return userAccountRepository.getPageOfUserAccounts(searchCriteria, requestedPage);
    }

    @Override
    public void banUsersAccounts(String... usersToBanUniqueNames) {
        userAccountRepository.banUsersAccounts(usersToBanUniqueNames);
    }

    @Override
    public void unlockUsersAccounts(String... usersToUnlockUniqueNames) {
        userAccountRepository.unlockUsersAccounts(usersToUnlockUniqueNames);
    }

    @Override
    public void deleteUsersAccounts(String... usersToDeleteUniqueNames){
        userAccountRepository.deleteUsersAccounts(usersToDeleteUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersToAcceptUniqueNames) {
        userAccountRepository.acceptUsersAccounts(usersToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames) {
        userAccountRepository.cancelAcceptUsersAccounts(usersToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        userAccountRepository.advancePlayersToOrganizer(playersToAdvanceToOrganizersUniqueNames);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        userAccountRepository.degradeOrganizerToPlayers(organizerToDegradeToPlayersUniqueNames);
    }
}
