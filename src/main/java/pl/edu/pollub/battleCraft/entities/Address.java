package pl.edu.pollub.battleCraft.entities;

import pl.edu.pollub.battleCraft.entities.enums.Provinces;

import javax.persistence.*;

@Entity
public class Address {

    public Address() {
    }

    public Address(Provinces province, String city, String street, String zip_code, AddressOwner addressOwner) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.zip_code = zip_code;
        this.addressOwner = addressOwner;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 30)
    private Provinces province;

    @Column(length = 40)
    private String city;

    @Column(length = 40)
    private String street;

    @Column(length = 5)
    private String zip_code;


    @OneToOne(mappedBy = "address")
    private AddressOwner addressOwner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Provinces getProvince() {
        return province;
    }

    public void setProvince(Provinces province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public AddressOwner getAddressOwner() {
        return addressOwner;
    }

    public void setAddressOwner(AddressOwner addressOwner) {
        this.addressOwner = addressOwner;
    }
}
