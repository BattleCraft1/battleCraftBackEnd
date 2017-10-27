package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.start;

public class ThisTournamentIsNotStarted extends RuntimeException{
    public ThisTournamentIsNotStarted(String tournamentName){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" is not started.").toString());
    }
}
