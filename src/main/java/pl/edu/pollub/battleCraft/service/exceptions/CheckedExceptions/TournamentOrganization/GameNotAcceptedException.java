package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization;

public class GameNotAcceptedException extends RuntimeException{
    public GameNotAcceptedException(String gameName){
        super(new StringBuilder("You cannot create tournament with game: ").append(gameName).append(" because this game is not accepted").toString());
    }
}
