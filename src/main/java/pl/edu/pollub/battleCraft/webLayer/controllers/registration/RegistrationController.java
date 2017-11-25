package pl.edu.pollub.battleCraft.webLayer.controllers.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.RegistrationService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void registration(@RequestBody RegistrationDTO registrationDTO, BindingResult bindingResult, HttpServletRequest request) {
        registrationService.register(registrationDTO,bindingResult,request);
    }

    @GetMapping(value = "/registration/resendToken")
    @ResponseStatus(HttpStatus.OK)
    public void resendToken(@RequestParam(value = "name") String name, HttpServletRequest request){
        registrationService.resendToken(name,request);
    }

    @GetMapping(value = "/registration/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirmRegistration(@RequestParam("token") String token){
        registrationService.confirmRegistration(token);
    }
}
