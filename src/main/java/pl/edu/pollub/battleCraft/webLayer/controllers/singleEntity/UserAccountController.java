package pl.edu.pollub.battleCraft.webLayer.controllers.singleEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.UserAccountService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.UserAccountToResponseDTOMapper;

@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    private final UserAccountToResponseDTOMapper userAccountToResponseDTOMapper;

    @Autowired
    public UserAccountController(UserAccountService userAccountService, UserAccountToResponseDTOMapper userAccountToResponseDTOMapper){
        this.userAccountService = userAccountService;
        this.userAccountToResponseDTOMapper = userAccountToResponseDTOMapper;
    }

    @PostMapping(value = "/edit/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserAccountResponseDTO editUserAccount(@RequestBody UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        return userAccountToResponseDTOMapper.map(userAccountService.editUserAccount(userAccountRequestDTO, bindingResult));
    }

    @GetMapping(value = "/get/user")
    public UserAccountResponseDTO getUserAccount(@RequestParam(value = "name") String name){
        return userAccountToResponseDTOMapper.map(userAccountService.getUserAccount(name));
    }
}