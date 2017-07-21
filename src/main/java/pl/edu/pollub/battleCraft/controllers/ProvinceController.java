package pl.edu.pollub.battleCraft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.entities.Province;
import pl.edu.pollub.battleCraft.services.ProvinceService;

import java.util.List;

@RestController
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/get/allProvinces/names")
    public List<String> getAllProvincesNames(){
        return provinceService.getAllProvincesNames();
    }
}
