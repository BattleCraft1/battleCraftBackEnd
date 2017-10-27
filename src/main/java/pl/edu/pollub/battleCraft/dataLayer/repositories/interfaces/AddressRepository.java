package pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;


@Repository
@Transactional
public interface AddressRepository extends JpaRepository<Address, Long> {

}