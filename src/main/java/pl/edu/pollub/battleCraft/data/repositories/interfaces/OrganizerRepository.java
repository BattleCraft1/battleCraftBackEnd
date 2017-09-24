package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
    @Modifying
    @Query("UPDATE Organizer o " +
            "SET o.status = 'ACCEPTED' WHERE o.name in ?1 AND o.status = 'ORGANIZER'")
    void degradeOrganizersToPlayers(String... organizerToDegradeToPlayersUniqueNames);

    @Modifying
    @Query("DELETE FROM Organization o WHERE (SELECT og.name FROM Organizer og WHERE og.banned = true AND og.id=o.organizer) IN ?1")
    void deleteOrganizationOfTournaments(String... playersToDeleteUniqueNames);

    @Modifying
    @Query("UPDATE Game g SET g.creator = null WHERE (SELECT og.name FROM Organizer og WHERE og.banned = true AND og.id=g.creator) IN ?1")
    void deleteCreationOfGames(String... playersToDeleteUniqueNames);
}
