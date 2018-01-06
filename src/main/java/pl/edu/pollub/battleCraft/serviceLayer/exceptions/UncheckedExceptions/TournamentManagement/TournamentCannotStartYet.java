package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

import java.util.Date;

public class TournamentCannotStartYet extends TournamentManagementException{
    public TournamentCannotStartYet(String tournamentName,Date dateOfStart){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" cannot start before: ").append(dateOfStart).toString());
    }
}
