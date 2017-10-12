package pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TournamentWebDTO {

    public TournamentWebDTO(Tournament tournament){
        String[] organizersNames = tournament.getOrganizers().stream()
                .map(organization -> organization.getOrganizer().getName())
                .toArray(String[]::new);

        String[] participantsNames = tournament.getParticipants().stream()
                .map(participation -> participation.getPlayer().getName())
                .toArray(String[]::new);

        this.name = tournament.getName();
        this.tablesCount = tournament.getTablesCount();
        this.maxPlayers = tournament.getMaxPlayers();
        this.game = tournament.getGame().getName();
        this.dateOfStart = tournament.getDateOfStart();
        this.dateOfEnd = tournament.getDateOfEnd();
        this.province = tournament.getAddress().getProvince().name();
        this.city = tournament.getAddress().getCity();
        this.street = tournament.getAddress().getStreet();
        this.zipCode = tournament.getAddress().getZipCode();
        this.description = tournament.getAddress().getDescription();
        this.organizers = organizersNames;
        this.participants = participantsNames;
    }

    public String name;
    public String nameChange;
    public int tablesCount;
    public int maxPlayers;
    public String game;
    public Date dateOfStart;
    public Date dateOfEnd;
    public String province;
    public String city;
    public String street;
    public String zipCode;
    public String description;
    public String[] organizers;
    public String[] participants;
}
