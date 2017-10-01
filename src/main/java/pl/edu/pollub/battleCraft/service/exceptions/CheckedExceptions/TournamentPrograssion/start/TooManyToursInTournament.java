package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.start;

public class TooManyToursInTournament extends RuntimeException {
    public TooManyToursInTournament(int maxToursNumber){
        super(new StringBuilder("Max tours number in this tournament is: ").append(maxToursNumber).toString());
    }
}
