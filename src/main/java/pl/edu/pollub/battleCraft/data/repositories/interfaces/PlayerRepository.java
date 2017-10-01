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
    @Query("UPDATE Player p SET p.banned = false WHERE p.name in ?1")
    void unlockUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.banned = true WHERE p.name in ?1")
    void banUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p " +
            "SET p.status = 'NEW' WHERE p.name in ?1")
    void cancelAcceptUsersAccounts(String... playersToCancelAcceptUniqueNames);

    @Query("SELECT p FROM Player p WHERE p.status = 'ACCEPTED' AND p.name in ?1")
    List<Player> findAllPlayersByName(String... playersToAdvanceToOrganizersUniqueNames);

    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT pl.name FROM Player pl WHERE pl.banned = true AND pl.id=p.player) IN ?1")
    void deleteParticipationInTournaments(String... playersToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Play p WHERE (SELECT pl.name FROM Player pl WHERE pl.banned = true AND pl.id = p.player) IN ?1")
    void deletePlayInTournaments(String... playersToDeleteUniqueNames);
}
