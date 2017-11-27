package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.mappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;

@Component
public class UserAccountToPlayerMapper {

    public Player map(UserAccount userAccount){
        if(!(userAccount instanceof Player)) {
            Player player = new Player();
            player.setFirstname(userAccount.getFirstname());
            player.setLastname(userAccount.getLastname());
            player.setName(userAccount.getName());
            player.setEmail(userAccount.getEmail());
            player.setPassword(userAccount.getPassword());
            player.setPhoneNumber(userAccount.getPhoneNumber());
            player.initAddress(userAccount.getAddress().copy());

            return player;
        }
        else{
            userAccount.setStatus(UserType.ACCEPTED);
            return (Player)userAccount;
        }
    }

}
