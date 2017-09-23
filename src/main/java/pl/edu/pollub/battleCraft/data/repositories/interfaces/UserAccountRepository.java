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
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @Modifying
    @Query("DELETE FROM UserAccount u WHERE u.name in" +
            "(SELECT name FROM  UserAccount u1 " +
            "WHERE ((u1.banned = true AND (u1.status = 'ACCEPTED' OR u1.status = 'ORGANIZER'))" +
            "OR u1.status = 'NEW') AND u.name in ?1)")
    void deleteUserAccounts(String... usersAccountsToDeleteUniqueNames);

    @Query("SELECT u FROM UserAccount u WHERE u.status = 'NEW' AND u.name in ?1")
    List<UserAccount> findAllUsersAccountsByName(String... usersAccountsToAcceptUniqueNames);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.addressOwner in (SELECT u.id FROM UserAccount u WHERE u.name in ?1)")
    void deleteRelatedAddress(String... usersAccountsToAcceptUniqueNames);
}
