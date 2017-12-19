package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisPlayerHasBattleInThisTurn extends TournamentManagementException{
    public ThisPlayerHasBattleInThisTurn(String playerName, int tourNumber){
        super(new StringBuilder("Player: ").append(playerName).append(" already has battle in Turn: ").append(tourNumber+1).toString());
    }
}
