package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TournamentRequestDTO {
    public String name;
    public String nameChange;
    public int tablesCount;
    public int maxPlayers;
    public String game;
    public Date dateOfStart;
    public Date dateOfEnd;
    public String province;
    public String city;
    public String street;
    public String zipCode;
    public String description;
    public String[] organizers;
    public String[] participants;
}
