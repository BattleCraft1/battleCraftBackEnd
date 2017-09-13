package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

public interface PlayerRepository extends JpaSpecificationExecutor<Player>, JpaRepository<Player, Long> {
    @Modifying
    @Query("UPDATE Player p SET p.banned = false WHERE p.username in ?1")
    void unlockUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p SET p.banned = true WHERE p.username in ?1")
    void banUserAccounts(String... playersToBanUniqueNames);

    @Modifying
    @Query("UPDATE Player p " +
            "SET p.class = UserAccount, p.userType = 'NEW' WHERE p.username in ?1 AND p.userType = 'PLAYER' AND p.class = Player")
    void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames);

    @Modifying
    @Query("UPDATE Player p " +
            "SET p.class = Organizer, p.userType = 'ORGANIZER' WHERE p.username in ?1 AND p.userType = 'PLAYER' AND p.class = Player")
    void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames);
}
