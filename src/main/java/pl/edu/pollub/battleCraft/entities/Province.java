package pl.edu.pollub.battleCraft.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/*I made field location as new Entity class because I wanted to create a list of possible provinces in poland in database*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Province {

    public Province(String location) {
        this.location = location;
    }

    public Province(String location,List<Address> addresses) {
        this.addresses = addresses;
        this.location = location;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column(length = 30)
    String location;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "province", cascade = CascadeType.ALL)
    List<Address> addresses;

}
