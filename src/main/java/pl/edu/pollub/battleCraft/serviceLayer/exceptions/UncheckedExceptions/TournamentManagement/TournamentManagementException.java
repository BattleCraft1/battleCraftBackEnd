package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public abstract class TournamentManagementException extends RuntimeException{
    protected TournamentManagementException(String message){
        super(message);
    }
}
