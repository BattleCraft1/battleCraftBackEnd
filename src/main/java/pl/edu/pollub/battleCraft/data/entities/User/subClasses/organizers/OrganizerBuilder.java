package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.UserBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.PlayerBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OrganizerBuilder{
    private Organizer instance;

    public OrganizerBuilder(){

    }

    public OrganizerBuilder create(String name, String surname, String username, String email, String password){
        this.instance = new Organizer();
        this.instance.setName(name);
        this.instance.setSurname(surname);
        this.instance.setUsername(username);
        this.instance.setEmail(email);
        this.instance.setPassword(password);
        return this;
    }


    public OrganizerBuilder organize(Tournament... tournaments){
        this.instance = new Organizer();
        (this.instance).setOrganizedTournaments(
                Arrays.stream(tournaments).map(tournament -> {
                    tournament.addOrganizers((Organizer)instance);
                    return new Organization((instance),tournament);})
                        .collect(Collectors.toList())
        );
        return this;
    }

    public OrganizerBuilder participateTo(Tournament... tournaments){
        (this.instance).setParticipatedTournaments(
                Arrays.stream(tournaments).map(tournament -> {
                    tournament.addParticipants((Player)instance);
                    return new Participation((instance),tournament);})
                        .collect(Collectors.toList())
        );
        return this;
    }

    public OrganizerBuilder withPhoneNumber(String phoneNumber){
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public OrganizerBuilder from(Address address){
        this.instance.setAddress(address);
        return this;
    }

    public Organizer build(){
        return instance;
    }
}
