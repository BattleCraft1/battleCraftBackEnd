package pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;

import java.util.List;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT t.name FROM Tournament t WHERE t.id=p.participatedTournament) IN ?1")
    void deleteParticipationByTournamentsUniqueNames(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Organization o WHERE (SELECT t.name FROM Tournament t WHERE t.id=o.organizedTournament) IN ?1")
    void deleteOrganizationByTournamentsUniqueNames(String... tournamentsToDeleteUniqueNames);

    @Query("SELECT t.name FROM Tournament t WHERE t.banned = true AND t.name in ?1")
    List<String> selectTournamentsToDeleteUniqueNames(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.name in ?1")
    void deleteTournamentsByUniqueNames(String... tournamentsToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = false WHERE t.name in ?1")
    void unlockTournamentsByUniqueNames(String... tournamentsToBanUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = true WHERE t.name in ?1")
    void banTournamentsByUniqueNames(String... tournamentsToBanUniqueNames);

    @Query("SELECT t.name FROM Tournament t WHERE t.name in ?1 AND t.status = 'NEW' AND t.banned = false")
    List<String> selectTournamentsToAcceptUniqueNames(String... tournamentsToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.status = 'ACCEPTED' WHERE t.name in ?1")
    void acceptTournamentsByUniqueNames(String... tournamentsToAcceptUniqueNames);

    @Query("SELECT t.name FROM Tournament t WHERE t.name in ?1 AND t.status = 'ACCEPTED' AND t.banned = false")
    List<String> selectTournamentsToRejectUniqueNames(String... tournamentsToRejectUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.status = 'NEW' WHERE t.name in ?1")
    void cancelAcceptTournamentsByUniqueNames(String... tournamentsToCancelAcceptUniqueNames);

    @Query("SELECT t.id FROM Tour t WHERE (SELECT tr.name FROM Tournament tr WHERE tr.id = t.tournament) IN ?1")
    List<Long> selectIdsOfToursToDeleteByTournamentsUniqueNames(String... tournamentsToDeleteUniqueNames);

    @Query("SELECT b.id FROM Battle b WHERE b.tour.id in ?1")
    List<Long> selectIdsOfBattlesToDeleteByToursIds(List<Long> idsOfToursToDelete);

    @Modifying
    @Query("DELETE FROM Play p WHERE p.battle.id in ?1")
    void deletePlaysByBattlesIds(List<Long> idsOfBattlesToDelete);

    @Modifying
    @Query("DELETE FROM Battle b WHERE b.id in ?1")
    void deleteBattlesByIds(List<Long> idsOfBattlesToDelete);

    @Modifying
    @Query("DELETE FROM Tour t WHERE t.id in ?1")
    void deleteToursByIds(List<Long> idsOfToursToDelete);

    @Query("SELECT t.id FROM Tournament t WHERE t.game = (SELECT g.id FROM Game g WHERE g.banned = true AND g.name in ?1)")
    List<Long> selectTournamentsIdsByGameUniqueNames(String... gamesToDeleteUniqueNames);

    @Modifying
    @Query("DELETE FROM Participation p WHERE (SELECT t.id FROM Tournament t WHERE t.id=p.participatedTournament) IN ?1")
    void deleteParticipationByTournamentsIds(List<Long> tournamentsToDeleteIds);

    @Modifying
    @Query("DELETE FROM Organization o WHERE (SELECT t.id FROM Tournament t WHERE t.id=o.organizedTournament) IN ?1")
    void deleteOrganizationByTournamentsIds(List<Long> tournamentsToDeleteIds);

    @Query("SELECT t.id FROM Tour t WHERE (SELECT tr.id FROM Tournament tr WHERE tr.id = t.tournament) IN ?1")
    List<Long> selectIdsOfToursToDeleteByTournamentsIds(List<Long> tournamentsToDeleteIds);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.id in ?1")
    void deleteTournamentsByIds(List<Long> tournamentsToDeleteIds);

    @Query("SELECT t FROM Tournament t Where t.name = ?1")
    Tournament findTournamentToEditByUniqueName(String tournamentUniqueName);

    @Query("SELECT t FROM Tournament t Where t.name in ?1 and (t.status='ACCEPTED' or t.status='NEW') and t.banned = false")
    List<Tournament> findAcceptedOrNewTournamentsByUniqueNames(List<String> tournamentUniqueNames);

    @Query("SELECT t FROM Tournament t Where t.name = ?1 and t.status='ACCEPTED' and t.banned = false")
    Tournament findAcceptedTournamentByUniqueName(String tournamentUniqueNames);

    Tournament findByName(String name);
}
