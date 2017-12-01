package pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;

import java.util.List;

@Repository
@Transactional
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @Modifying
    @Query("DELETE FROM UserAccount u WHERE u.id in ?1")
    void deleteUsersAccountsByIds(List<Long> usersAccountsToDeleteIds);

    @Query("SELECT u FROM UserAccount u WHERE u.status = 'NEW' AND u.name in ?1")
    List<UserAccount> selectUsersAccountsToAcceptByUniqueNames(String... usersAccountsToAcceptUniqueNames);

    @Query("SELECT u FROM UserAccount u WHERE u.status = 'NEW' AND u.name = ?1")
    UserAccount findNotAcceptedUserByUniqueName(String uniqueName);

    @Query("SELECT u FROM UserAccount u WHERE u.status = 'NEW' AND u.email = ?1")
    UserAccount findNotAcceptedUserByEmail(String email);

    @Query("SELECT u FROM UserAccount u WHERE u.email = ?1")
    UserAccount findUserByEmail(String email);

    @Query("SELECT u FROM UserAccount u WHERE u.name in ?1")
    List<UserAccount> findAllUsersAccountsByUniqueName(String... usersAccountsToAcceptUniqueNames);

    @Modifying
    @Query("DELETE FROM Address a WHERE (SELECT ao.id FROM AddressOwner ao WHERE ao.address=a.id) in ?1")
    void deleteRelatedAddress(List<Long> usersAccountsToDeleteIds);

    @Query("SELECT u FROM UserAccount u WHERE u.name in ?1")
    List<UserAccount> selectUsersAccountsToDeleteByUniqueNames(String... usersAccountsToAcceptUniqueNames);

    @Query("SELECT u.id FROM UserAccount u WHERE u.name in ?1")
    List<Long> selectIdsOfUsersToDelete(String... usersAccountsToAcceptUniqueNames);

    @Query("SELECT u FROM UserAccount u WHERE u.name = ?1")
    UserAccount findUserAccountByUniqueName(String userUniqueName);

    @Query("SELECT u FROM UserAccount u WHERE u.name = ?1 OR u.email = ?2")
    UserAccount findUserAccountByUniqueNameOrEmail(String userUniqueName, String email);

    @Query("SELECT u FROM UserAccount u WHERE NOT u.name = ?1 AND u.email = ?2")
    UserAccount findUserAccountByEmailWithOtherUniqueName(String userUniqueName, String email);

    @Query("SELECT u FROM UserAccount u WHERE u.name = ?1")
    UserAccount checkIfUserExist(String username);

    UserAccount findByName(String name);
}
