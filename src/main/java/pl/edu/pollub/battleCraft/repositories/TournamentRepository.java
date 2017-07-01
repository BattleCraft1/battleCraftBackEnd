package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.entities.Tournament;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t")
    Page<Tournament> getTournamentsFromPage(Pageable page);
}
