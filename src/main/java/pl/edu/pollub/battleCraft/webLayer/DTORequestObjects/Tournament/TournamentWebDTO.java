package pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament;

import java.util.Date;
import java.util.List;

public class TournamentWebDTO {
    public String name;
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
