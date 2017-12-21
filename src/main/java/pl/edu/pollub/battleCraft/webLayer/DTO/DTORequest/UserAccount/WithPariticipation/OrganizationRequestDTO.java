package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRequestDTO {
    protected String name;
    protected boolean accepted;

    public String getTournamentName(){
        return this.name;
    }
}

