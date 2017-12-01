package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;

import java.util.stream.Collectors;

@Component
public class PlayerToOrganizerMapper {

    public Organizer map(Player player){
            Organizer organizer = new Organizer();
            organizer.setFirstname(player.getFirstname());
            organizer.setLastname(player.getLastname());
            organizer.setName(player.getName());
            organizer.setEmail(player.getEmail());
            organizer.setPassword(player.getPassword());
            organizer.setPhoneNumber(player.getPhoneNumber());
            organizer.setParticipation(player.getParticipation().stream().map(participation -> {
                    participation.setPlayer(organizer);
                    return participation.copy();
            }).collect(Collectors.toList()));

            organizer.initAddress(player.getAddress().copy());

            return organizer;
    }

}
