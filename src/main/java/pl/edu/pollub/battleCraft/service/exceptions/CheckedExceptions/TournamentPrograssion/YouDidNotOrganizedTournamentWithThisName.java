package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion;

public class YouDidNotOrganizedTournamentWithThisName extends RuntimeException{
    public YouDidNotOrganizedTournamentWithThisName(String tournamentName){
        super(new StringBuilder("You did not organized tournament with name: ").append(tournamentName).toString());
    }
}
