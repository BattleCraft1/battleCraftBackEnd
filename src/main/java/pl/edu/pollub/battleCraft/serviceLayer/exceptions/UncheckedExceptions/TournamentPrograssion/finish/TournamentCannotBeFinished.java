package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.finish;

public class TournamentCannotBeFinished extends RuntimeException{
    public TournamentCannotBeFinished(){
        super("This tournament have not finished tours");
    }
}
