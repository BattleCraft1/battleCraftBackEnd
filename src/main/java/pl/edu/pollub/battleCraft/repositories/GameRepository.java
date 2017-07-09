package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.edu.pollub.battleCraft.entities.Game;

public interface GameRepository extends JpaSpecificationExecutor<Game>, JpaRepository<Game, Long> {
}
