package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import java.util.List;

public interface UserAccountService {
    Page getPageOfUserAccounts(Pageable pageable, List<SearchCriteria> searchCriteria);

    List<String> getAllUserTypes();
}
