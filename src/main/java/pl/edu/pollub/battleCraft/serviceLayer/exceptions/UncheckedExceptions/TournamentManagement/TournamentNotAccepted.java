package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class TournamentNotAccepted extends TournamentManagementException{
    public TournamentNotAccepted(String name){
        super(new StringBuilder("Tournament: ").append(name).append(" is not accepted").toString());
    }
}
