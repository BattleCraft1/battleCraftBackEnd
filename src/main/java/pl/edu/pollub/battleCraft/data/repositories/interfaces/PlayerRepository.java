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
    void unlockUserAccountsByUniqueNames(String... usersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.banned = true WHERE p.name in ?1")
    void banUserAccountsByUniqueNames(String... usersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.status = 'NEW' WHERE p.name in ?1")
    void cancelAcceptationOfUsersAccountsByUniqueNames(String... usersToCancelAcceptUniqueNames);

    @Query("SELECT p FROM Player p WHERE p.status = 'ACCEPTED' AND p.name in ?1")
    List<Player> findAllPlayersByName(String... playersNames);

    @Modifying
    @Query("DELETE FROM Participation p WHERE p.player.id IN ?1")
    void deleteParticipationByPlayersIds(List<Long> playersIds);

    @Modifying
    @Query("DELETE FROM Play p WHERE p.player.id IN ?1")
    void deletePlayByPlayersIds(List<Long> playersIds);
}
