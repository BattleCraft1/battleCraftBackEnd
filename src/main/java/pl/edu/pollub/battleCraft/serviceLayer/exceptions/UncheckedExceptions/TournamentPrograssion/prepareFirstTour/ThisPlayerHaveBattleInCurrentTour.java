package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.prepareFirstTour;

public class ThisPlayerHaveBattleInCurrentTour extends RuntimeException{
    public ThisPlayerHaveBattleInCurrentTour(String playerName){
        super(new StringBuilder("Player: ").append(playerName).append(" already have battle in this tour").toString());
    }
}
