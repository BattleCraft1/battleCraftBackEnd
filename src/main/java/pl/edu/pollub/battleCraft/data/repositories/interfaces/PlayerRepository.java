package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Modifying
    @Query("UPDATE Player p SET p.banned = false WHERE p.username in ?1")
    void unlockUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.banned = true WHERE p.username in ?1")
    void banUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p " +
            "SET p.userType = 'NEW' WHERE p.username in ?1")
    void cancelAcceptUsersAccounts(String... playersToCancelAcceptUniqueNames);

    @Query("SELECT p FROM Player p WHERE type(p) = Player AND p.userType = 'PLAYER' AND p.username in ?1")
    List<Player> findAllPlayersByUsername(String... playersToAdvanceToOrganizersUniqueNames);
}
