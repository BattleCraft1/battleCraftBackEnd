package pl.edu.pollub.battleCraft.webLayer.controllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.UsersAccountsService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.UserAccountResourcesService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageAndModifyDataDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageObjectsDTO;

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
    public Page getPageOfTournaments(@RequestBody GetPageObjectsDTO getPageObjectsDTO) {
        System.out.println("Try to get users");
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/ban/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banUsersAccounts(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.banUsersAccounts(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/unlock/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockUsersAccounts(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.unlockUsersAccounts(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteUsersAccounts(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) throws IOException {
        userAccountService.deleteUsersAccounts(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        userAccountResourcesService.deleteUsersAccountsAvatars(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/accept/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptUsersAccounts(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.acceptUsersAccounts(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/cancel/accept/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptUsersAccounts(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.cancelAcceptUsersAccounts(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/advance/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page advancePlayersToOrganizer(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.advancePlayersToOrganizer(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/degrade/organizers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page degradeOrganizerToPlayers(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO){
        userAccountService.degradeOrganizerToPlayers(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return userAccountService.getPageOfUserAccounts(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }
}
