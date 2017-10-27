package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.start;

public class YouDidNotOrganizeTournamentWithThisName extends RuntimeException{
    public YouDidNotOrganizeTournamentWithThisName(String tournamentName){
        super(new StringBuilder("You did not organized tournament with name: ").append(tournamentName).toString());
    }
}
