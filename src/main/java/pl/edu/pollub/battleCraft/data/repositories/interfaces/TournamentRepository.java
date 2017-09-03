package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TournamentRepository extends JpaSpecificationExecutor<Tournament>, JpaRepository<Tournament, Long> {
    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT t.name FROM Tournament t WHERE t.banned = true AND t.id=p.tournament) IN ?1")
    void deleteParticipationOfTournaments(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.banned = true AND t.name in ?1")
    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = false WHERE t.name in ?1")
    void unlockTournaments(String... tournamentsToBanUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = true WHERE t.name in ?1")
    void banTournaments(String... tournamentsToBanUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.tournamentStatus = 'ACCEPTED' WHERE t.name in ?1 AND t.tournamentStatus = 'NEW' AND t.banned = false")
    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.tournamentStatus = 'NEW' WHERE t.name in ?1 AND t.tournamentStatus = 'ACCEPTED' AND t.banned = false")
    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);
}
