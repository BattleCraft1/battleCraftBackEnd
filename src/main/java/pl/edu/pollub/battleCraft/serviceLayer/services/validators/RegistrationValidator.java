package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.UserAccountWithInvitationsRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

@Component
public class RegistrationValidator implements Validator {

    private final UserAccountValidator userAccountValidator;

    private Errors errors;

    public RegistrationValidator(UserAccountValidator userAccountValidator) {
        this.userAccountValidator = userAccountValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RegistrationDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        RegistrationDTO registrationDTO = (RegistrationDTO) o;
        this.validatePassword(registrationDTO.getPassword(),registrationDTO.getPasswordConfirm());
        userAccountValidator.validate(registrationDTO,errors);
    }

    private void validatePassword(String password, String passwordConfirm){
        if(!password.equals(passwordConfirm)){
            this.errors.rejectValue("passwordConfirm","","Password confirmation and password are not the same");
        }
    }

    public void checkIfUserWithThisNameAlreadyExist(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        userAccountValidator.checkIfUserWithThisNameAlreadyExist(userAccountRequestDTO,bindingResult);
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid user account data", bindingResult);
        }
    }
}
