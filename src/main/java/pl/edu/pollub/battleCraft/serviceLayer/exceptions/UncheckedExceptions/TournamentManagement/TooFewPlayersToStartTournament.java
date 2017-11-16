package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TooFewPlayersToStartTournament extends TournamentManagementException  {
    public TooFewPlayersToStartTournament(String tournamentName) {
        super(new StringBuilder("You have too few players to start tournament: ").append(tournamentName).toString());
    }
}
