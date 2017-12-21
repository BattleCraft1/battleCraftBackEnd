package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountWithParticipationRequestDTO extends UserAccountRequestDTO {

    private List<ParticipationRequestDTO> participatedTournaments = new ArrayList<>();
    private List<OrganizationRequestDTO> organizedTournaments = new ArrayList<>();
}
