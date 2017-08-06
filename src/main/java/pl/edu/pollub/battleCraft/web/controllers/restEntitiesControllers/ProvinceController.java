package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;

import java.util.List;

@RestController
public class ProvinceController {
    private final ProvinceService provinceService;

    @Autowired
    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping("/get/allProvinces/names")
    public List<String> getAllProvincesNames(){
        return provinceService.getAllProvincesNames();
    }
}
