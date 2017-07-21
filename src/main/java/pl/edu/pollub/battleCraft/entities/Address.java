package pl.edu.pollub.battleCraft.entities;

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
public class Address {


    public Address( String city, String street, String zip_code) {
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

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn
    private Province province;

    @Column(length = 40)
    private String city;

    @Column(length = 40)
    private String street;

    @Column(length = 6)
    private String zip_code;

    @JsonIgnore
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private AddressOwner addressOwner;

}
