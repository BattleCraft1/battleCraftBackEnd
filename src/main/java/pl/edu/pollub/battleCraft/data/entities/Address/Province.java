package pl.edu.pollub.battleCraft.data.entities.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Province{

    @Column(length = 30)
    String location;

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    public Province(String location) {
        this.location = location;
    }
}
