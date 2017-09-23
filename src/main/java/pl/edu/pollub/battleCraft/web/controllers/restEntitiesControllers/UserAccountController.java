package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;
import pl.edu.pollub.battleCraft.service.services.interfaces.UserAccountService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageObjectsWrapper;

import java.awt.image.BufferedImage;
import java.io.IOException;
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

    @PostMapping(value = "/ban/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banUsersAccounts(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.banUsersAccounts(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/unlock/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockUsersAccounts(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.unlockUsersAccounts(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/delete/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteUsersAccounts(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) throws IOException {
        userAccountService.deleteUsersAccounts(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/accept/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptUsersAccounts(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.acceptUsersAccounts(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/cancel/accept/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptUsersAccounts(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.cancelAcceptUsersAccounts(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/advance/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page advancePlayersToOrganizer(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.advancePlayersToOrganizer(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/degrade/organizers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page degradeOrganizerToPlayers(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        userAccountService.degradeOrganizerToPlayers(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return userAccountService.getPageOfUserAccounts(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping("/get/users/types")
    public List<String> getAllUsersTypes() {
        return userAccountService.getAllUserTypes();
    }

    @GetMapping("/get/users/enums")
    public Map<String, List<String>> getUsersEnums() {
        List<String> usersTypes = userAccountService.getAllUserTypes();
        List<String> provincesNames = provinceService.getAllProvincesNames();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("usersTypes", usersTypes);
        enums.put("provincesNames", provincesNames);
        return enums;
    }

    @GetMapping("/get/user/{name}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable String name) throws IOException {
        byte[] image = userAccountService.getUserAvatar(name);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
