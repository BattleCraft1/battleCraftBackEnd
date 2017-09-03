package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedUserAccountRepository;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;
import pl.edu.pollub.battleCraft.service.services.interfaces.UserAccountService;

import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final ExtendedUserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountServiceImpl(ExtendedUserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Page getPageOfUserAccounts(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return userAccountRepository.getPageOfUserAccounts(new SearchSpecification<>(searchCriteria), requestedPage);
    }

    @Override
    public List<String> getAllUserTypes() {
        return UserType.getNames();
    }
}
