package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisPlayersAreNotInTheSameGroup extends TournamentManagementException{
    public ThisPlayersAreNotInTheSameGroup(String firstPlayerName,String secondPlayerName){
        super(new StringBuilder("Players: ").append(firstPlayerName).append(" and ").append(secondPlayerName)
                .append(" are not in the same group").toString());
    }
}
