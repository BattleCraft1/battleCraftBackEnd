package pl.edu.pollub.battleCraft.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AddressOwner {


    public AddressOwner(Address address) {
        this.address = address;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn
    private Address address;
}
