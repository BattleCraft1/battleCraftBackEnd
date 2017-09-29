package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization;

public class TooBigMaxPlayersCount extends RuntimeException{
    public TooBigMaxPlayersCount(int maxPlayers,int tablesCount){
        super(new StringBuilder("You cannot create tournament with ")
                .append(maxPlayers)
                .append(" max players count because if you have ")
                .append(tablesCount)
                .append(" you can have only ")
                .append(tablesCount*2)
                .append(" players").toString());
    }
}
