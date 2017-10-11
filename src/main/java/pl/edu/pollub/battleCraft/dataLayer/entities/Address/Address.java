package pl.edu.pollub.battleCraft.dataLayer.entities.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Address implements Cloneable{

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Enumerated(EnumType.STRING)
    private Province province;

    @Column(length = 40)
    private String city;

    @Column(length = 40)
    private String street;

    @Column(length = 6)
    private String zipCode;

    @JsonIgnore
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private AddressOwner addressOwner;

    @Column(length = 100)
    private String description;

    public Address(String province, String city, String street, String zipCode, String description){
        this.province = Province.valueOf(province);
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.description = description;
    }

    private Address(Province province, String city, String street, String zipCode, String description) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.description = description;
    }

    public void setAddressOwnerByOneSide(AddressOwner addressOwner){
        this.addressOwner = addressOwner;
    }

    private void setAddressOwner(AddressOwner addressOwner){
        this.addressOwner = addressOwner;
    }

    public Address clone(){
        return new Address(this.province,this.city,this.street,this.zipCode,this.description);
    }
}
