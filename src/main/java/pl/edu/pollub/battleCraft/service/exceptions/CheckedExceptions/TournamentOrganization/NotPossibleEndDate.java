package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization;

public class NotPossibleEndDate extends RuntimeException {
    public NotPossibleEndDate(String startDate){
        super(new StringBuilder("End date must be later than ").append(startDate).toString());
    }
}
