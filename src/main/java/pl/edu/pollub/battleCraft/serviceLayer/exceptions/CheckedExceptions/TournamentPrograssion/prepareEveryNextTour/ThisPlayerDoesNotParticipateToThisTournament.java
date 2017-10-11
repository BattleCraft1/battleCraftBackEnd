package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class ThisPlayerDoesNotParticipateToThisTournament extends RuntimeException{
    public ThisPlayerDoesNotParticipateToThisTournament(String playerName){
        super(new StringBuilder("Player: ").append(playerName).append(" not participate to this tournament").toString());
    }
}
