package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ItIsFirstTourOfTournament extends TournamentManagementException{
    public ItIsFirstTourOfTournament(String tournamentName){
        super(new StringBuilder("It is first tour of tournament: ").append(tournamentName).toString());
    }
}
