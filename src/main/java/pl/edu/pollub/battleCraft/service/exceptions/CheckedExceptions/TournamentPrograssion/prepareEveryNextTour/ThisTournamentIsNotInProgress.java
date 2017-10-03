package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class ThisTournamentIsNotInProgress extends RuntimeException{
    public ThisTournamentIsNotInProgress(String tournamentName){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" is not in progress").toString());
    }
}
