package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class TournamentIsFinished extends RuntimeException{
    public TournamentIsFinished(String tournamentName){
        super(new StringBuilder("All tours in tournament: ").append(tournamentName).append(" are finished").toString());
    }
}
