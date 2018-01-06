package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement;

public class ThisTableIsReservedForAlonePlayer extends TournamentManagementException{
    public ThisTableIsReservedForAlonePlayer(){
        super("This table is reserved for alone player");
    }
}
