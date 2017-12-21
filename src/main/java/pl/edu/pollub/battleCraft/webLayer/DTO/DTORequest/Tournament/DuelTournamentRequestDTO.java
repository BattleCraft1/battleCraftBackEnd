package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
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
public class DuelTournamentRequestDTO extends TournamentRequestDTO{
    private List<String> participants = new ArrayList<>();
}
