package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT DISTINCT g.name from Game g")
    List<String> getAllGamesNames();

    @Modifying
    @Query("DELETE FROM Game g WHERE g.banned = true AND g.name in ?1")
    void deleteGames(String... gamesToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.banned = false WHERE g.name in ?1")
    void unlockGames(String... gamesToBanUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.banned = true WHERE g.name in ?1")
    void banGames(String... gamesToBanUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.status = 'ACCEPTED' WHERE g.name in ?1 AND g.status = 'NEW' AND g.banned = false")
    void acceptGames(String... gamesToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.status = 'NEW' WHERE g.name in ?1 AND g.status = 'ACCEPTED' AND g.banned = false")
    void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames);
}
