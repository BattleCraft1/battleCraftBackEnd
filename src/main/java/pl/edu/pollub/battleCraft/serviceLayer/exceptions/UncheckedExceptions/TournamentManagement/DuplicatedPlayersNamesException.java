package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class DuplicatedPlayersNamesException extends TournamentManagementException{
    public DuplicatedPlayersNamesException(){
        super("Duplicated players names detected");
    }
}
