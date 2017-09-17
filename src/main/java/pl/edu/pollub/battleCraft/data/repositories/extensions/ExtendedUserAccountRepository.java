package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface ExtendedUserAccountRepository {
    Page getPageOfUserAccounts(List<SearchCriteria> searchCriteria, Pageable requestedPage);

    void banUsersAccounts(String... usersAccountsToBanUniqueNames);

    void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames);

    void unlockUsersAccounts(String... usersAccountsToBanUniqueNames);

    void acceptUsersAccounts(String... usersAccountsToAcceptUniqueNames);

    void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames);

    void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames);

    void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames);
}
