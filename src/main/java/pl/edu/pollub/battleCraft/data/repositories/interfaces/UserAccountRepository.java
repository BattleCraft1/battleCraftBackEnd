package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserAccountRepository extends JpaSpecificationExecutor<UserAccount>, JpaRepository<UserAccount, Long> {
}
