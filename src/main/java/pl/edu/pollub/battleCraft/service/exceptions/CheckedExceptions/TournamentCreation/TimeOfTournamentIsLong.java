package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentCreation;

public class TimeOfTournamentIsLong extends RuntimeException{
    public TimeOfTournamentIsLong(){
        super("Duration of tournament cannnot be longer than 3 days");
    }
}
