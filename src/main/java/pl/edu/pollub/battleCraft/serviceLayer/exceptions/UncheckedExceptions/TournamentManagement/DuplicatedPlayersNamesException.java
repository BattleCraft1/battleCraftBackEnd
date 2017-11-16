package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class DuplicatedPlayersNamesException extends RuntimeException{
    public DuplicatedPlayersNamesException(){
        super("Duplicated players names detected");
    }
}
