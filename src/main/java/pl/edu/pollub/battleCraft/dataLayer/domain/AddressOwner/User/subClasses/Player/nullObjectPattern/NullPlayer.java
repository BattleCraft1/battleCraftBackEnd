package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.nullObjectPattern;

import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;

public class NullPlayer extends Player {
    public NullPlayer(){
        super();
        this.name = "";
        this.firstname = "";
        this.lastname = "";
        this.email = "";
        this.password = "";
        this.phoneNumber = "";
    }
}
