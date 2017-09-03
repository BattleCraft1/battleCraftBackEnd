package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;
import pl.edu.pollub.battleCraft.service.services.interfaces.UserAccountService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageObjectsWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    private final ProvinceService provinceService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService, ProvinceService provinceService) {
        this.userAccountService = userAccountService;
        this.provinceService = provinceService;
    }

    @PostMapping(value = "/page/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper) {
        System.out.println("Try to get users");
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping("/get/users/types")
    public List<String> getAllProvincesNames() {
        return userAccountService.getAllUserTypes();
    }

    @GetMapping("/get/users/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        List<String> usersTypes = userAccountService.getAllUserTypes();
        List<String> provincesNames = provinceService.getAllProvincesNames();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("usersTypes", usersTypes);
        enums.put("provincesNames", provincesNames);
        return enums;
    }
}
