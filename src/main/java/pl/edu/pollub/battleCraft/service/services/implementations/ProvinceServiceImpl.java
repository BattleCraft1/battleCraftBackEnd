package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.ProvinceRepository;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;

    @Autowired
    public ProvinceServiceImpl(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    public List<String> getAllProvincesNames() {
        return provinceRepository.getAllProvincesNames();
    }
}
