package pl.edu.pollub.battleCraft.data.entities.User.subClasses.admins;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@DiscriminatorValue(value= UserType.Values.ADMIN)
@SecondaryTable(name = "administrator", pkJoinColumns = {@PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")})

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Administrator extends UserAccount{
}
