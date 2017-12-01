package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TooFewOrganizersToStartTournament extends TournamentManagementException  {
    public TooFewOrganizersToStartTournament(String tournamentName) {
        super(new StringBuilder("You have too few organizers to start tournament: ").append(tournamentName).toString());
    }
}
