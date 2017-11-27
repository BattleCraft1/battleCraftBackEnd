package pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;

@Repository
@Transactional
public interface AdminRepository extends JpaRepository<Administrator, Long> {
}
