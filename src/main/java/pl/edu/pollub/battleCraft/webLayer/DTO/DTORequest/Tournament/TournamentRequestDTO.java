package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<List<String>> participants = new ArrayList<>();
    private List<String> organizers;

    public TournamentType getTournamentType(){
        if(playersOnTableCount==2){
            return TournamentType.DUEL;
        }
        return TournamentType.GROUP;
    }
}
