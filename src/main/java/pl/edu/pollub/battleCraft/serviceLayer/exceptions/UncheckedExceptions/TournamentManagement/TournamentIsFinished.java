package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TournamentIsFinished extends TournamentManagementException{
    public TournamentIsFinished(String tournamentName){
        super(new StringBuilder("All tours in tournament: ").append(tournamentName).append(" is finished").toString());
    }
}

