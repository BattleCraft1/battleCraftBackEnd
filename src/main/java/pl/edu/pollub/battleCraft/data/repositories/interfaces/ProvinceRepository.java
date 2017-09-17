package pl.edu.pollub.battleCraft.data.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.pollub.battleCraft.data.entities.Address.Province;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    @Query("SELECT p.location from Province p")
    List<String> getAllProvincesNames();
}
