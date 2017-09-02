package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.UserAccountService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageObjectsWrapper;

import java.util.List;

@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService){
        this.userAccountService = userAccountService;
    }

    @PostMapping(value = "/page/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper){
        System.out.println("Try to get users");
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping("/get/users/types")
    public List<String> getAllProvincesNames(){
        return userAccountService.getAllUserTypes();
    }
}
