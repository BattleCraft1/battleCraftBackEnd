package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserAccountRepository extends JpaSpecificationExecutor<UserAccount>, JpaRepository<UserAccount, Long> {
    @Modifying
    @Query("DELETE FROM UserAccount u WHERE u.username in" +
            "(SELECT username FROM  UserAccount u1 " +
            "WHERE ((type(u1) = Player AND u1.banned = true AND u1.userType = 'PLAYER')" +
            "OR (type(u1) = UserAccount AND u1.userType = 'NEW')) AND u.username in ?1)")
    void deleteUserAccounts(String... usersAccountsToDeleteUniqueNames);

    @Query("SELECT u FROM UserAccount u WHERE type(u) = UserAccount AND u.userType = 'NEW' AND u.username in ?1")
    List<UserAccount> findAllUsersAccountsByUsername(String... usersAccountsToAcceptUniqueNames);
}
