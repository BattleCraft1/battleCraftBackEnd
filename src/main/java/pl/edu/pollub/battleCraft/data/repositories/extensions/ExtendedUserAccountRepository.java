package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

public interface ExtendedUserAccountRepository {
    Page getPageOfUserAccounts(SearchSpecification<UserAccount> objectSearchSpecification, Pageable requestedPage);
}
