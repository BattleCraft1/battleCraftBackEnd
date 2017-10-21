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
    public String name;
    public String nameChange;
    public int tablesCount;
    public int playersOnTableCount;
    public String game;
    public Date dateOfStart;
    public Date dateOfEnd;
    public String[] organizers;
    public String[] participants;
    public String status;
}
