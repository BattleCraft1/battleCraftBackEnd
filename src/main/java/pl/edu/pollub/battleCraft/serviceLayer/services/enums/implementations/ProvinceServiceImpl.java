package pl.edu.pollub.battleCraft.serviceLayer.services.enums.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Province;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.ProvinceService;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {

    public List<String> getAllProvincesNames() {
        return Province.getNames();
    }
}
