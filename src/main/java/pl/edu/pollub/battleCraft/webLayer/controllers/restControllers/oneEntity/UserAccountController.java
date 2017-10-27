package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.oneEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.UserAccountService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService){
        this.userAccountService = userAccountService;
    }

    @PostMapping(value = "/edit/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserAccountResponseDTO editUserAccount(@RequestBody UserAccountRequestDTO userAccountWebDTO, BindingResult bindingResult){
        return userAccountService.editUserAccount(userAccountWebDTO, bindingResult);
    }

    @GetMapping(value = "/get/user")
    public UserAccountResponseDTO getUserAccount(@RequestParam(value = "name") String name){
        return userAccountService.getUserAccount(name);
    }
}
