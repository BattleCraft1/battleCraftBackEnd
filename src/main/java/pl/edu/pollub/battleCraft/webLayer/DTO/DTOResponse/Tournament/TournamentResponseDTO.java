package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TournamentResponseDTO {
    private String name;
    private String nameChange;
    private int toursCount;
    private int tablesCount;
    private int playersOnTableCount;
    private String game;
    private Date dateOfStart;
    private Date dateOfEnd;
    private String province;
    private String city;
    private String street;
    private String zipCode;
    private String description;
    private String status;
    private List<InvitationDTO> organizers = new ArrayList<>();
    private List<InvitationDTO> participants = new ArrayList<>();
}
