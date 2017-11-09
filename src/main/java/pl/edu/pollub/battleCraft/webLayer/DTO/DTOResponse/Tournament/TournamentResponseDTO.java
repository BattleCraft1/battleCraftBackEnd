package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<InvitationResponseDTO> organizers = new ArrayList<>();
    private List<List<InvitationResponseDTO>> participants = new ArrayList<>();
}
