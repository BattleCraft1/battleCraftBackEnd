package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;

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
