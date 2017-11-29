package pl.edu.pollub.battleCraft.webLayer.controllers.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.RegistrationService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.EmailDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void registration(@RequestBody RegistrationDTO registrationDTO, BindingResult bindingResult) {
        registrationService.register(registrationDTO,bindingResult);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/create/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void createAdminAccount(@RequestBody RegistrationDTO registrationDTO, BindingResult bindingResult) {
        registrationService.createAdminAccount(registrationDTO,bindingResult);
    }

    @PostMapping(value = "/registration/resendToken")
    @ResponseStatus(HttpStatus.OK)
    public void resendToken(@RequestBody EmailDTO emailResendDTO){
        registrationService.resendToken(emailResendDTO);
    }

    @GetMapping(value = "/registration/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirmRegistration(@RequestParam("token") String token){
        registrationService.confirmRegistration(token);
    }
}
