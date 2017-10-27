package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentPrograssion.start;

public class TooManyToursInTournament extends RuntimeException {
    public TooManyToursInTournament(int maxToursNumber){
        super(new StringBuilder("Max tours number in this tournament is: ").append(maxToursNumber).toString());
    }
}
