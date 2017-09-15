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
public interface OrganizerRepository extends JpaSpecificationExecutor<Organizer>, JpaRepository<Organizer, Long> {
    @Modifying
    @Query("UPDATE Organizer o " +
            "SET o.class = Player, o.userType = 'PLAYER' WHERE o.username in ?1 AND o.userType = 'ORGANIZER'")
    void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames);
}
