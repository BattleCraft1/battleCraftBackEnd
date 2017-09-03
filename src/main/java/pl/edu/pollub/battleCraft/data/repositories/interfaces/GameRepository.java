package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GameRepository extends JpaSpecificationExecutor<Game>, JpaRepository<Game, Long> {
    @Query("SELECT DISTINCT g.name from Game g")
    List<String> getAllGamesNames();
}
