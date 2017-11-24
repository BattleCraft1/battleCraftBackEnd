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
    List<String> selectTournamentsNamesToDeleteUniqueNames(String... tournamentsToDeleteUniqueNames);

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
    List<String> selectTournamentsNamesToAcceptUniqueNames(String... tournamentsToAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Tournament t SET t.status = 'ACCEPTED' WHERE t.name in ?1")
    void acceptTournamentsByUniqueNames(String... tournamentsToAcceptUniqueNames);

    @Query("SELECT t.name FROM Tournament t WHERE t.name in ?1 AND t.status = 'ACCEPTED' AND t.banned = false")
    List<String> selectTournamentsNamesToRejectUniqueNames(String... tournamentsToRejectUniqueNames);

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

    @Query("SELECT t.name FROM Tournament t Where t.name = ?1")
    String checkIfTournamentWithThisNameAlreadyExist(String tournamentUniqueName);

    @Query("SELECT t FROM Tournament t " +
            "LEFT JOIN FETCH t.game g " +
            "LEFT JOIN FETCH t.address a " +
            "LEFT JOIN FETCH t.participation p " +
            "LEFT JOIN FETCH p.participationGroup gr " +
            "LEFT JOIN FETCH p.player pl " +
            "LEFT JOIN FETCH pl.address plAd " +
            "LEFT JOIN FETCH t.organizations o " +
            "LEFT JOIN FETCH o.organizer org " +
            "LEFT JOIN FETCH org.address orgAd "+
            "LEFT JOIN FETCH t.tours tr " +
            "LEFT JOIN FETCH tr.battles b " +
            "LEFT JOIN FETCH b.players pl " +
            "Where t.name = ?1")
    Tournament fetchEagerTournamentByUniqueName(String tournamentUniqueName);

    @Query("SELECT t FROM Tournament t " +
            "LEFT JOIN FETCH t.game g " +
            "LEFT JOIN FETCH t.address a " +
            "LEFT JOIN FETCH t.participation p " +
            "LEFT JOIN FETCH p.participationGroup gr " +
            "LEFT JOIN FETCH gr.participationInGroup pInGr " +
            "LEFT JOIN FETCH p.player pl " +
            "LEFT JOIN FETCH pl.address plAd " +
            "LEFT JOIN FETCH t.organizations o " +
            "LEFT JOIN FETCH o.organizer org " +
            "LEFT JOIN FETCH org.address orgAd "+
            "Where t.name = ?1")
    Tournament fetchEagerTournamentWithoutToursByUniqueName(String tournamentUniqueName);

    @Query("SELECT t FROM Tournament t Where t.name in ?1 and (t.status='ACCEPTED' or t.status='NEW') and t.banned = false")
    List<Tournament> findAcceptedOrNewTournamentsByUniqueNames(List<String> tournamentUniqueNames);

    @Query("SELECT t FROM Tournament t " +
            "LEFT JOIN FETCH t.participation p " +
            "LEFT JOIN FETCH t.organizations o " +
            "Where t.name in ?1 and (t.status='ACCEPTED' or t.status='NEW') and t.banned = false")
    List<Tournament> fetchEagerAcceptedOrNewTournamentsByUniqueNames(List<String> tournamentUniqueNames);

    @Query("SELECT t FROM Tournament t Where (t.status='ACCEPTED' or t.status='NEW') and t.banned = false")
    List<Tournament> findAllAcceptedOrNewTournaments();

    @Query("SELECT t FROM Tournament t " +
            "LEFT JOIN FETCH t.participation p " +
            "LEFT JOIN FETCH t.organizations o  " +
            "LEFT JOIN FETCH t.tours tr  " +
            "LEFT JOIN FETCH tr.battles b  " +
            "LEFT JOIN FETCH b.players pl " +
            "Where t.name = ?1 and t.status='IN_PROGRESS' and t.banned = false")
    Tournament findStartedTournamentByUniqueName(String tournamentUniqueNames);

    @Query("SELECT t FROM Tournament t Where t.status='IN_PROGRESS' and t.banned = false")
    List<Tournament> findAllStartedTournament();

    Tournament findByName(String name);

    @Modifying
    @Query("UPDATE Tournament t SET t.banned = true WHERE (SELECT g.name FROM Game g WHERE t.game=g.id) IN ?1")
    void banTournamentsRelatedWithGame(String... bannedGamesList);
}
