package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;

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
        organizer.setParticipation(
                player.getParticipation().stream().map(Participation::copy).collect(Collectors.toSet()));

        organizer.setAddressOnTwoSides(player.getAddress().copy());

        return organizer;
    }

}
