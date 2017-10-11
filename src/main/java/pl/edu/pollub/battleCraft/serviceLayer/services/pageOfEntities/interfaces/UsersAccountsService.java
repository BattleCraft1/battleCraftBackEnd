package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.io.IOException;
import java.util.List;

public interface UsersAccountsService {
    Page getPageOfUserAccounts(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banUsersAccounts(String... usersToBanUniqueNames);

    void unlockUsersAccounts(String... usersUnlockUniqueNames);

    void deleteUsersAccounts(String... usersToDeleteUniqueNames) throws IOException;

    void acceptUsersAccounts(String... usersToAcceptUniqueNames);

    void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames);

    void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames);

    void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames);
}
