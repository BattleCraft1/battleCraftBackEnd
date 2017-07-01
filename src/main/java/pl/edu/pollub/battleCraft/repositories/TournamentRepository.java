package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.entities.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
