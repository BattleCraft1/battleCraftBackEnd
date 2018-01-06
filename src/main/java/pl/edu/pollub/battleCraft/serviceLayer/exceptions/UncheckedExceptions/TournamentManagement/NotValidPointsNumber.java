package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class NotValidPointsNumber extends TournamentManagementException{
    public NotValidPointsNumber(){
        super("Points number should be between 0 to 20 and summary of points not should be greater than 20");
    }
}
