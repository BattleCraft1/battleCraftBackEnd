package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareFirstTour;

public class BattleWithTableNumberNotFound extends RuntimeException {
    public BattleWithTableNumberNotFound(int tableNumber){
        super(new StringBuilder("Battle with table number: ").append(tableNumber).append(" not exist").toString());
    }
}
