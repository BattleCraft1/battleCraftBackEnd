package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour;

public class NotValidPointsNumber extends RuntimeException{
    public NotValidPointsNumber(){
        super("Points number must be between 0 and 20");
    }
}