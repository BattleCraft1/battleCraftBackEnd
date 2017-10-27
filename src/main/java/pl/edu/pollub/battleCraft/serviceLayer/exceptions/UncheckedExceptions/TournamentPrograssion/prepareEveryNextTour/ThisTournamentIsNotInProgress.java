package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class ThisTournamentIsNotInProgress extends RuntimeException{
    public ThisTournamentIsNotInProgress(String tournamentName){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" is not in progress").toString());
    }
}
