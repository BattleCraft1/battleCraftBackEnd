package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentCreation;

public class OutdatedStartDate extends RuntimeException{
    public OutdatedStartDate(String date){
        super(new StringBuilder("you cannot start tournament at: ").append(date).append(" because this date is outdated").toString());
    }
}
