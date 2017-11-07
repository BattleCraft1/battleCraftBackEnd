package pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.List;

@Repository
@Transactional
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT DISTINCT g.name from Game g WHERE g.banned = false AND g.status = 'ACCEPTED'")
    List<String> getAllAcceptedGamesNames();

    @Query("SELECT g.name FROM Game g WHERE g.banned = true AND g.name in ?1")
    List<String> selectGamesToDeleteUniqueNames(String... gamesToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Game g WHERE g.name in ?1")
    void deleteGamesByUniqueNames(String... gamesToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.banned = false WHERE g.name in ?1")
    void unlockGamesByUniqueNames(String... gamesToBanUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.banned = true WHERE g.name in ?1")
    void banGamesByUniqueNames(String... gamesToBanUniqueNames);

    @Query("SELECT g.name FROM Game g WHERE g.name in ?1 AND g.status = 'NEW' AND g.banned = false")
    List<String> selectGamesToAcceptUniqueNames(String... gamesToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.status = 'ACCEPTED' WHERE g.name in ?1")
    void acceptGamesByUniqueNames(String... gamesToAcceptUniqueNames);

    @Query("SELECT g.name FROM Game g WHERE g.name in ?1 AND g.status = 'ACCEPTED' AND g.banned = false")
    List<String> selectGamesToRejectUniqueNames(String... gamesToCancelAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.status = 'NEW' WHERE g.name in ?1")
    void cancelAcceptGamesUniqueNames(String... gamesToCancelAcceptUniqueNames);

    @Query("SELECT g from Game g WHERE g.name = ?1 and g.status = 'ACCEPTED' and g.banned = false")
    Game findAcceptedGameByUniqueName(String gameName);

    @Query("SELECT g from Game g WHERE g.name = ?1 and g.banned = false")
    Game findNotBannedGameByUniqueName(String gameName);

    @Query("SELECT g from Game g WHERE g.name = ?1")
    Game findGameByUniqueName(String gameName);

    @Query("SELECT g.name from Game g WHERE g.name = ?1 and g.banned = false")
    String checkIfGameExist(String gameName);
}
