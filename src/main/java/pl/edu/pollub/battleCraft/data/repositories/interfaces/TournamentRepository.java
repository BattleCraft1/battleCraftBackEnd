package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT t.name FROM Tournament t WHERE t.banned = true AND t.id=p.participatedTournament) IN ?1")
    void deleteParticipationInTournaments(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Organization o WHERE (SELECT t.name FROM Tournament t WHERE t.banned = true AND t.id=o.organizedTournament) IN ?1")
    void deleteOrganizationOfTournaments(String... tournamentsToDeleteUniqueNames);

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
    @Query("UPDATE Tournament t SET t.status = 'ACCEPTED' WHERE t.name in ?1 AND t.status = 'NEW' AND t.banned = false")
    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.status = 'NEW' WHERE t.name in ?1 AND t.status = 'ACCEPTED' AND t.banned = false")
    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);

    @Query("SELECT t.id FROM Tour t WHERE (SELECT tr.name FROM Tournament tr WHERE tr.banned = true AND tr.id = t.tournament) IN ?1")
    List<Long> selectIdsOfToursToDelete(String[] tournamentsToDeleteUniqueNames);

    @Query("SELECT b.id FROM Battle b WHERE b.tour in ?1")
    List<Long> selectIdsOfBattlesToDelete(List<Long> idsOfToursToDelete);

    @Modifying
    @Query("DELETE FROM Play p WHERE p.battle in ?1")
    void deletePlaysOfTournaments(List<Long> idsOfBattlesToDelete);

    @Modifying
    @Query("DELETE FROM Battle b WHERE b.id in ?1")
    void deleteBattlesOfTournaments(List<Long> idsOfBattlesToDelete);

    @Modifying
    @Query("DELETE FROM Tour t WHERE t.id in ?1")
    void deleteToursOfTournaments(List<Long> idsOfToursToDelete);
}
