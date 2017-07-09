package pl.edu.pollub.battleCraft.exceptions;

public class PageNotFoundException extends Exception{
    public PageNotFoundException(String className,int pageNumber){
        super(new StringBuilder("Page ").append(pageNumber).append(" of ").append(className).append(" not found").toString());
    }

    public PageNotFoundException(int pageNumber){
        super(new StringBuilder("Page ").append(pageNumber).append(" not found").toString());
    }
}
