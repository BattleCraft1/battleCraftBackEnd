package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class BattleOnTableNotFinishedYet extends RuntimeException{
    public BattleOnTableNotFinishedYet(int tableNumber){
        super(new StringBuilder("Battle on table: ").append(tableNumber).append(" is not finished yet").toString());
    }
}
