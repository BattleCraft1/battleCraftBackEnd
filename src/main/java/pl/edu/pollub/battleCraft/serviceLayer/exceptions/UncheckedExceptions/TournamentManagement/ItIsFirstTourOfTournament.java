package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ItIsFirstTourOfTournament extends TournamentManagementException{
    public ItIsFirstTourOfTournament(String tournamentName){
        super(new StringBuilder("It is first turn of tournament: ").append(tournamentName).toString());
    }
}
