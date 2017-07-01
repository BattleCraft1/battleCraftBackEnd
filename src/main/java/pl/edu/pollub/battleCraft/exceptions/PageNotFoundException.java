package pl.edu.pollub.battleCraft.exceptions;

import pl.edu.pollub.battleCraft.entities.Tournament;

/**
 * Created by Jarek on 2017-07-01.
 */
public class PageNotFoundException extends Exception{
    public PageNotFoundException(String className,int pageNumber){
        super(new StringBuilder("Page ").append(pageNumber).append(" of ").append(className).append(" not found").toString());
    }
}
