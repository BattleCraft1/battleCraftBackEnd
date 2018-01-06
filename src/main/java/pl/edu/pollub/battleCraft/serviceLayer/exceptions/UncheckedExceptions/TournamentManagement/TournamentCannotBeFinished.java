package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TournamentCannotBeFinished extends TournamentManagementException{
    public TournamentCannotBeFinished(String tournamentName){
        super(new StringBuilder("Tournament: ").append(tournamentName).append("have not finished turns").toString());
    }
}
