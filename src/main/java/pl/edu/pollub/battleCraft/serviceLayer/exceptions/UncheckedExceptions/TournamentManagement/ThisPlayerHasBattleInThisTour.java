package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisPlayerHasBattleInThisTour extends TournamentManagementException{
    public ThisPlayerHasBattleInThisTour(String playerName, int tourNumber){
        super(new StringBuilder("Player: ").append(playerName).append(" already has battle in Tour: ").append(tourNumber+1).toString());
    }
}
