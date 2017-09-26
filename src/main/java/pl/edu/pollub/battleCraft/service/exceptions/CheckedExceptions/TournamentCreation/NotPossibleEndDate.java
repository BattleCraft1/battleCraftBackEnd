package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentCreation;

public class NotPossibleEndDate extends RuntimeException {
    public NotPossibleEndDate(String startDate){
        super(new StringBuilder("End date must be later than ").append(startDate).toString());
    }
}
