package pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AddressRepository extends JpaRepository<Address, Long> {

}