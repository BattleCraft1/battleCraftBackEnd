package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization;

public class TimeOfTournamentIsLong extends RuntimeException{
    public TimeOfTournamentIsLong(){
        super("Duration of tournament cannnot be longer than 3 days");
    }
}
