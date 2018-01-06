package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TournamentIsFinished extends TournamentManagementException{
    public TournamentIsFinished(String tournamentName){
        super(new StringBuilder("All turns in tournament: ").append(tournamentName).append(" is finished").toString());
    }
}

