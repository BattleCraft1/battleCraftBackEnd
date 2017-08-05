package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Tournament;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public interface TournamentRepository extends JpaSpecificationExecutor<Tournament>, JpaRepository<Tournament, Long> {
    @Modifying
    @Query("UPDATE Tournament t SET t.banned = true WHERE t.name in ?1")
    void banTournaments(List<String> tournamentsToBanUniqueNames);

    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT t.name FROM Tournament t WHERE t.id=p.tournament) IN ?1")
    void deleteParticipationOfTournaments(List<String> tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.name in ?1")
    void deleteTournaments(List<String> tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = false WHERE t.name in ?1")
    void unlockTournaments(List<String> tournamentsToBanUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.accepted = true WHERE t.name in ?1")
    void acceptTournaments(ArrayList<String> tournamentsToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.accepted = false WHERE t.name in ?1")
    void cancelAcceptTournaments(ArrayList<String> tournamentsToCancelAcceptUniqueNames);
}
