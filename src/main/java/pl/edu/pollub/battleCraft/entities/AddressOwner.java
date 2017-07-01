package pl.edu.pollub.battleCraft.entities;

import javax.persistence.*;

/**
 * Created by Jarek on 2017-07-01.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AddressOwner {

    public AddressOwner() {
    }

    public AddressOwner(Address address) {
        this.address = address;
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
