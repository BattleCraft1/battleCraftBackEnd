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
    @Query("DELETE FROM UserAccount u WHERE u.id in ?1")
    void deleteUsersAccountsByIds(List<Long> usersAccountsToDeleteIds);

    @Query("SELECT u FROM UserAccount u WHERE u.status = 'NEW' AND u.name in ?1")
    List<UserAccount> findAllUsersAccountsByUniqueName(String... usersAccountsToAcceptUniqueNames);

    @Modifying
    @Query("DELETE FROM Address a WHERE (SELECT ao.id FROM AddressOwner ao WHERE ao.address=a.id) in ?1")
    void deleteRelatedAddress(List<Long> usersAccountsToDeleteIds);

    @Query("SELECT u.id FROM UserAccount u WHERE u.name in" +
            "(SELECT name FROM  UserAccount u1 " +
            "WHERE ((u1.banned = true AND (u1.status = 'ACCEPTED' OR u1.status = 'ORGANIZER'))" +
            "OR u1.status = 'NEW') AND u.name in ?1)")
    List<Long> selectIdsOfUsersToDelete(String... usersAccountsToAcceptUniqueNames);
}
