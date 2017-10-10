package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.io.IOException;
import java.util.List;

public interface UserAccountService {
    Page getPageOfUserAccounts(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banUsersAccounts(String... usersToBanUniqueNames);

    void unlockUsersAccounts(String... usersUnlockUniqueNames);

    void deleteUsersAccounts(String... usersToDeleteUniqueNames) throws IOException;

    void acceptUsersAccounts(String... usersToAcceptUniqueNames);

    void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames);

    void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames);

    void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames);

    List<String> getAllUserTypes();

    byte[] getUserAvatarWeb(String userId) throws IOException;

    byte[] getUserAvatarMobile(String userId) throws IOException;
}
