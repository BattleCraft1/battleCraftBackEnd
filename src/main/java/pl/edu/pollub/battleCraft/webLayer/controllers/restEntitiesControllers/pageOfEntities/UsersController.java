package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.UsersAccountsService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.UserAccountResourcesService;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageObjectsWrapper;

import java.io.IOException;

@RestController
public class UsersController {

    private final UsersAccountsService userAccountService;

    private final UserAccountResourcesService userAccountResourcesService;

    @Autowired
    public UsersController(UsersAccountsService userAccountService, UserAccountResourcesService userAccountResourcesService) {
        this.userAccountService = userAccountService;
        this.userAccountResourcesService = userAccountResourcesService;
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
        userAccountResourcesService.deleteUsersAccountsAvatars(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
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
}
