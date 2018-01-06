package pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccount;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

@Component
public class RegistrationValidator implements Validator {

    private final UserAccountValidator userAccountValidator;

    private final UserAccountRepository userAccountRepository;

    private Errors errors;

    public RegistrationValidator(UserAccountValidator userAccountValidator, UserAccountRepository userAccountRepository) {
        this.userAccountValidator = userAccountValidator;
        this.userAccountRepository = userAccountRepository;
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
        if (password.length() < 8 || password.length() > 32) {
            errors.rejectValue("password", "", "Password should have more than 8 characters and less than 32");
        }
    }

    public void checkIfUserWithThisNameAlreadyExist(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        UserAccount userExist = userAccountRepository.findUserAccountByUniqueNameOrEmail(userAccountRequestDTO.getNameChange(), userAccountRequestDTO.getEmail());
        if(userExist!=null){
            if (userExist.getName().equals(userAccountRequestDTO.getName()))
                bindingResult.rejectValue("nameChange", "", "User with this name already exist.");
            if (userExist.getEmail().equals(userAccountRequestDTO.getEmail()))
                bindingResult.rejectValue("email", "", "User with this email already exist.");
        }
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid user account data", bindingResult);
        }
    }
}
