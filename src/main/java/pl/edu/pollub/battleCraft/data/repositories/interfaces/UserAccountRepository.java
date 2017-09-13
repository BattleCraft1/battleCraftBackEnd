package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserAccountRepository extends JpaSpecificationExecutor<UserAccount>, JpaRepository<UserAccount, Long> {
    @Modifying
    @Query("DELETE FROM UserAccount u WHERE u.username in" +
            "(SELECT username FROM  UserAccount u1 " +
            "WHERE u1.class = Player AND u1.banned = true AND u1.userType = 'PLAYER'" +
            "OR u1.class = UserAccount AND u1.userType = 'NEW')")
    void deleteUserAccounts(String... usersAccountsToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE UserAccount u " +
            "SET u.class = Player, u.userType = 'PLAYER'" +
            "WHERE u.class = UserAccount AND u.userType = 'NEW'")
    void acceptUserAccounts(String[] usersAccountsToAcceptUniqueNames);
}
