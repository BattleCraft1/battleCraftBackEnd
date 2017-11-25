package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.EntityNotFoundException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.UserAccountWithInvitationsRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

import java.util.Optional;

@Component
public class UserAccountValidator implements Validator {

    private Errors errors;

    private final AddressValidator addressValidator;

    private final UserAccountRepository userAccountRepository;

    public UserAccountValidator(AddressValidator addressValidator, UserAccountRepository userAccountRepository) {
        this.addressValidator = addressValidator;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserAccountRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        UserAccountWithInvitationsRequestDTO userAccountRequestDTO = (UserAccountWithInvitationsRequestDTO) o;
        this.validateName(userAccountRequestDTO.getName());
        this.validateName(userAccountRequestDTO.getNameChange());
        this.validateEMail(userAccountRequestDTO.getEmail());
        this.validateFirstName(userAccountRequestDTO.getFirstname());
        this.validateLastName(userAccountRequestDTO.getLastname());
        this.validatePhoneNumber(userAccountRequestDTO.getPhoneNumber());
        addressValidator.validate(userAccountRequestDTO,errors);
    }

    private void validateName(String name){
        if(name==null || !name.matches("^[A-ZĄĆĘŁŃÓŚŹŻa-zzżźćńółęąś1-9]{3,30}$"))
            errors.rejectValue("nameChange","","Name must start with big letter and have between 3 to 30 chars");
    }

    private void validateFirstName(String firstname){
        if(firstname==null || !firstname.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][a-zzżźćńółęąś]{2,19}$"))
            errors.rejectValue("firstname","","First name must start with big letter and have between 3 to 30 chars");
    }

    private void validateLastName(String lastname){
        if(lastname==null || !lastname.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][a-zzżźćńółęąś]{2,19}$"))
            errors.rejectValue("lastname","","Last name must start with big letter and have between 3 to 30 chars");
    }

    private void validatePhoneNumber(String phoneNumber){
        if(!phoneNumber.equals("") && !phoneNumber.matches("^[0-9]{9,11}$"))
            errors.rejectValue("phoneNumber","","Invalid phone number");
    }

    private void validateEMail(String email){
        if(email==null || !email.matches("(^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.+[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@+(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$)"))
            errors.rejectValue("email","","Invalid email");
    }

    public void checkIfUserWithThisNameAlreadyExist(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        if(!userAccountRequestDTO.getName().equals(userAccountRequestDTO.getNameChange())) {
            UserAccount userExist = userAccountRepository.findUserAccountByUniqueName(userAccountRequestDTO.getNameChange());
            if (userExist != null)
                bindingResult.rejectValue("nameChange", "", "User with this name already exist.");
        }
    }

    public UserAccount getValidatedUserAccountToEdit(UserAccountWithInvitationsRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        return Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userAccountRequestDTO.getName()))//TO DO: if player is administrator
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,userAccountRequestDTO.getName()));
    }
}
