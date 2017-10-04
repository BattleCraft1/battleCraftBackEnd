package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    @Modifying
    @Query("UPDATE Organizer o SET o.status = 'ACCEPTED' WHERE o.name in ?1 AND o.status = 'ORGANIZER'")
    void degradeOrganizersToPlayersByPlayersUniqueNames(String... organizerToDegradeToPlayersUniqueNames);

    @Modifying
    @Query("DELETE FROM Organization o WHERE o.organizer.id IN ?1")
    void deleteOrganizationByIds(List<Long> organizersIds);

    @Modifying
    @Query("UPDATE Game g SET g.creator = null WHERE g.creator.id IN ?1")
    void deleteCreationOfGamesByOrganizersIds(List<Long> organizersIds);

    @Query("SELECT o.id FROM Organizer o WHERE o.id in (SELECT og.organizer FROM Organization og WHERE og.organizedTournament.id in ?1)")
    List<Long> selectIdsOfOrganizersByTournamentsIds(List<Long> tournamentsToDeleteIds);
}
