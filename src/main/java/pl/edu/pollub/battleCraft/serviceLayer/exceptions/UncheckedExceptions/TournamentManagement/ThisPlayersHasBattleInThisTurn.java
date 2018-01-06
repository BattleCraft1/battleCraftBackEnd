package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisPlayersHasBattleInThisTurn extends RuntimeException{
    public ThisPlayersHasBattleInThisTurn(String firstPlayerName, String secondPlayerName, int tourNumber){
        super(new StringBuilder("Players: ").append(firstPlayerName).append(", ").append(secondPlayerName)
                .append(" already has battle in Turn: ").append(tourNumber+1).toString());
    }
}
