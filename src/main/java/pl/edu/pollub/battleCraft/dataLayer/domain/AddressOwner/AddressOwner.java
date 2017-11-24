package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Province;

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

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn
    private Address address;

    public AddressOwner(Address address) {
        this.address = address;
        address.setAddressOwner(this);
    }

    public void setAddressOnTwoSides(Address address){
        this.address = address;
        address.setAddressOwner(this);
    }

    public void changeAddress(String province, String city, String street, String zipCode, String description) {
        this.address.setProvince(Province.valueOf(province));
        this.address.setCity(city);
        this.address.setStreet(street);
        this.address.setZipCode(zipCode);
        this.address.setDescription(description);
    }
}
