package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.UserAccountEnumsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserEnumsController {

    private final UserAccountEnumsService userAccountEnumsService;

    @Autowired
    public UserEnumsController(UserAccountEnumsService userAccountEnumsService) {
        this.userAccountEnumsService = userAccountEnumsService;
    }

    @GetMapping("/get/users/types")
    public List<String> getAllUsersTypes() {
        return userAccountEnumsService.getAllUserTypes();
    }

    @GetMapping("/get/users/enums")
    public Map<String, List<String>> getUsersEnums() {
        List<String> usersTypes = userAccountEnumsService.getAllUserTypes();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("usersTypes", usersTypes);
        return enums;
    }
}
