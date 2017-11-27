package pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;

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

    @Query("SELECT p FROM Player p WHERE  p.status = 'ACCEPTED' and p.name in ?1")
    List<Player> selectUsersAccountsToRejectByUniqueNames(String... usersToCancelAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.status = 'NEW' WHERE p.name in ?1")
    void cancelAcceptationOfUsersAccountsByUniqueNames(String... usersToCancelAcceptUniqueNames);

    @Query("SELECT p FROM Player p WHERE p.status = 'ACCEPTED' AND p.name in ?1 and p.banned = false")
    List<Player> selectUsersAccountsToAdvanceByUniqueNames(String... playersNames);

    @Query("SELECT p FROM Player p WHERE p.name in ?1")
    List<Player> findPlayersByUniqueName(String... playersNames);


    @Query("SELECT p FROM Player p WHERE p.banned = false AND p.name in ?1")
    List<Player> findNotBannedPlayersByUniqueName(String... playersNames);

    @Query("SELECT p FROM Player p WHERE p.name in ?1")
    List<Player> findPlayersByUniqueName(List<String> playersNames);

    @Modifying
    @Query("DELETE FROM Participation p WHERE p.player.id IN ?1")
    void deleteParticipationByPlayersIds(List<Long> playersIds);

    @Modifying
    @Query("DELETE FROM Play p WHERE p.player.id IN ?1")
    void deletePlayByPlayersIds(List<Long> playersIds);

    @Query("SELECT p FROM Player p WHERE p.name = ?1 and p.banned = false")
    Player findNotBannedPlayerByUniqueName(String name);

}
