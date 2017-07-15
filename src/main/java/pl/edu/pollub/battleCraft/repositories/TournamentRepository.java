package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.entities.Tournament;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TournamentRepository extends JpaSpecificationExecutor<Tournament>, JpaRepository<Tournament, Long> {
    @Modifying
    @Query("UPDATE Tournament t SET t.banned = true WHERE t.name in ?1")
    void banTournaments(List<String> tournamentsToBanUniqueNames);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.name in ?1")
    void deleteTournaments(List<String> tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = false WHERE t.name in ?1")
    void unlockTournaments(List<String> tournamentsToBanUniqueNames);
}
