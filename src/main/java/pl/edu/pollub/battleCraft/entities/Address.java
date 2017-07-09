package pl.edu.pollub.battleCraft.entities;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;

@Entity
public class Address {

    public Address() {
    }

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
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private Province province;

    @Column(length = 40)
    private String city;

    @Column(length = 40)
    private String street;

    @Column(length = 6)
    private String zip_code;

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private AddressOwner addressOwner;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
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
