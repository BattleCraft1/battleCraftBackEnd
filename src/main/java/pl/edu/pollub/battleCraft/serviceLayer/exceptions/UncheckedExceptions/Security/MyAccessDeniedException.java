package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

public class MyAccessDeniedException extends RuntimeException{
    public MyAccessDeniedException(String msg){
        super(msg);
    }
}
