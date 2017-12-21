package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDTO{
    protected String name;
    protected boolean accepted;
    private String secondPlayerName;

    public String getTournamentName(){
        return this.name;
    }
}
