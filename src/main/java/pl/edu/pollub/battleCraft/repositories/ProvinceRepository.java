package pl.edu.pollub.battleCraft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.edu.pollub.battleCraft.entities.Province;

public interface ProvinceRepository extends JpaSpecificationExecutor<Province>, JpaRepository<Province, Long> {
}
