package pl.edu.pollub.battleCraft.data.entities.Address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Address implements Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Province province;
    @Column(length = 40)
    private String city;
    @Column(length = 40)
    private String street;
    @Column(length = 6)
    private String zip_code;
    @JsonIgnore
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private AddressOwner addressOwner;

    public Address(Province province, String city, String street, String zip_code) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.zip_code = zip_code;
    }

    public Address(Province province, String city, String street, String zip_code, AddressOwner addressOwner) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.zip_code = zip_code;
        this.addressOwner = addressOwner;
    }

    public void setAddressOwner(AddressOwner addressOwner) {
        this.addressOwner = addressOwner;
    }

    public void setAddressOwnerByOneSide(AddressOwner addressOwner){
        this.addressOwner = addressOwner;
    }
}
