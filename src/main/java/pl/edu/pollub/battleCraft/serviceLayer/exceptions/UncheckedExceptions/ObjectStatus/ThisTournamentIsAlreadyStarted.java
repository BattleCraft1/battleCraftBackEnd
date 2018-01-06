package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus;

public class ThisTournamentIsAlreadyStarted extends RuntimeException{
    public ThisTournamentIsAlreadyStarted(){
        super("You cannot edit started tournaments");
    }
}
