package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class InvalidTypeOfTournament extends TournamentManagementException{
    public InvalidTypeOfTournament(){
        super("Invalid type of tournament");
    }
}
