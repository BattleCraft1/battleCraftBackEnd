package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.admins;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.enums.UserType;

import javax.persistence.Entity;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Administrator extends UserAccount {
    public Administrator() {
        super(UserType.ADMIN);
    }
}
