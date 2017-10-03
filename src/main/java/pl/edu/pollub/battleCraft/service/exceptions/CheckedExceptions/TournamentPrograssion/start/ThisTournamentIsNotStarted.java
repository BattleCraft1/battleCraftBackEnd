package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.start;

public class ThisTournamentIsNotStarted extends RuntimeException{
    public ThisTournamentIsNotStarted(String tournamentName){
        super(new StringBuilder("Tournament: ").append(tournamentName).append(" is not started.").toString());
    }
}
