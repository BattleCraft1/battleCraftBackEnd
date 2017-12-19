package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwnership;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Province;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Embeddable
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AddressOwnership {

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn
    private Address address;

    public void insertAddress(Address address){
        this.address = address;
    }

    public void changeAddress(String province, String city, String street, String zipCode, String description) {
        this.address.setProvince(Province.valueOf(province));
        this.address.setCity(city);
        this.address.setStreet(street);
        this.address.setZipCode(zipCode);
        this.address.setDescription(description);
    }
}
