package pl.edu.pollub.battleCraft.entities;

import javax.persistence.*;

@Entity
public class Address {

    public Address() {
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "address")
    private AddressOwner addressOwner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
