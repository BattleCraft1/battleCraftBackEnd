package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;

import java.util.stream.Collectors;

@Service
public class PlayerToOrganizerMapper {

    public Organizer map(Player player){
        Organizer organizer = new Organizer();
        organizer.setFirstname(player.getFirstname());
        organizer.setLastname(player.getLastname());
        organizer.setName(player.getName());
        organizer.setEmail(player.getEmail());
        organizer.setPassword(player.getPassword());
        organizer.setPhoneNumber(player.getPhoneNumber());
        organizer.editParticipation(
                player.getParticipatedTournaments().stream().map(Participation::clone).collect(Collectors.toList()));
        try {
            organizer.initAddress((Address)player.getAddress().clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return organizer;
    }

}
