package pl.edu.pollub.battleCraft.data.entities.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Province implements Serializable {

    public Province(String location) {
        this.location = location;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column(length = 30)
    String location;
}
