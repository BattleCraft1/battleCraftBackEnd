package pl.edu.pollub.battleCraft.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.entities.Province;
import pl.edu.pollub.battleCraft.repositories.ProvinceRepository;
import pl.edu.pollub.battleCraft.services.ProvinceService;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService{
    @Autowired
    private ProvinceRepository provinceRepository;

    public List<String> getAllProvincesNames(){
        return provinceRepository.getAllProvincesNames();
    }
}
