package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisPlayersHasBattleInThisTour extends RuntimeException{
    public ThisPlayersHasBattleInThisTour(String firstPlayerName, String secondPlayerName, int tourNumber){
        super(new StringBuilder("Players: ").append(firstPlayerName).append(", ").append(secondPlayerName)
                .append(" already has battle in Tour: ").append(tourNumber+1).toString());
    }
}
