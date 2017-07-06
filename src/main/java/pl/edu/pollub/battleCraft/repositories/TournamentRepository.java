package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.entities.Tournament;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TournamentRepository extends JpaSpecificationExecutor<Tournament>, JpaRepository<Tournament, Long> {

}
