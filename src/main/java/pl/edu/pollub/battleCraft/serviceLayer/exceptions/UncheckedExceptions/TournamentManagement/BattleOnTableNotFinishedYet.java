package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentManagementException;

public class BattleOnTableNotFinishedYet extends TournamentManagementException {
    public BattleOnTableNotFinishedYet(int tourNumber, int tableNumber){
        super(new StringBuilder("Battle on table: ").append(tableNumber+1).append(" in tour number: ").append(tourNumber+1).append(" is not finished yet").toString());
    }
}
