package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.pollub.battleCraft.entities.Province;

import java.util.List;

public interface ProvinceRepository extends JpaSpecificationExecutor<Province>, JpaRepository<Province, Long> {
    @Query("SELECT p.location from Province p")
    List<String> getAllProvincesNames();
}
