package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.Invitation.InvitationDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TournamentResponseDTO {

    public TournamentResponseDTO(Tournament tournament){
        this.organizers = tournament.getOrganizers().stream()
                .map(organization -> {
                    Organizer organizer = organization.getOrganizer();
                    return new InvitationDTO(organizer.getName(),organization.isAccepted());
                })
                .collect(Collectors.toList());

        this.participants = tournament.getParticipants().stream()
                .map(participation -> {
                    Player player = participation.getPlayer();
                    return new InvitationDTO(player.getName(),participation.isAccepted());
                })
                .collect(Collectors.toList());

        this.name = tournament.getName();
        this.nameChange = tournament.getName();
        this.tablesCount = tournament.getTablesCount();
        this.playersOnTableCount = tournament.getPlayersOnTableCount();
        this.game = tournament.getGame().getName();
        this.dateOfStart = tournament.getDateOfStart();
        this.dateOfEnd = tournament.getDateOfEnd();
        this.province = tournament.getAddress().getProvince().name();
        this.city = tournament.getAddress().getCity();
        this.street = tournament.getAddress().getStreet();
        this.zipCode = tournament.getAddress().getZipCode();
        this.description = tournament.getAddress().getDescription();
    }

    public String name;
    public String nameChange;
    public int tablesCount;
    public int playersOnTableCount;
    public String game;
    public Date dateOfStart;
    public Date dateOfEnd;
    public String province;
    public String city;
    public String street;
    public String zipCode;
    public String description;
    public List<InvitationDTO> organizers = new ArrayList<>();
    public List<InvitationDTO> participants = new ArrayList<>();
}
