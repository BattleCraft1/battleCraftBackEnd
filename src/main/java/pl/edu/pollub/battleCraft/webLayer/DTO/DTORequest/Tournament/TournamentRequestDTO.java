package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TournamentRequestDTO extends AddressOwnerRequestDTO{
    private String name;
    private String nameChange;
    private int toursCount;
    private int tablesCount;
    private int playersOnTableCount;
    private String game;
    private Date dateOfStart;
    private Date dateOfEnd;
    private String[] organizers;
    private String[] participants;
}
