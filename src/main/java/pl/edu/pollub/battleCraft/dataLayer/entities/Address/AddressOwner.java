package pl.edu.pollub.battleCraft.dataLayer.entities.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AddressOwner{
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn
    private Address address;

    public AddressOwner(Address address) {
        this.address = address;
        address.setAddressOwnerByOneSide(this);
    }

    public void changeAddress(Address address){
        this.address = address;
        address.setAddressOwnerByOneSide(this);
    }

    private void setAddress(Address address){
        this.address = address;
    }
}
