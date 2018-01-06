package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

import java.util.Date;

public class TournamentIsOutOfDate extends TournamentManagementException{
    public TournamentIsOutOfDate(String tournamentName,Date dateOfEnd){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" should and before: ").append(dateOfEnd)
                .append(". From this reason it is banned.").toString());
    }
}
