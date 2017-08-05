package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Address;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AddressRepository extends JpaSpecificationExecutor<Address>, JpaRepository<Address, Long> {

}