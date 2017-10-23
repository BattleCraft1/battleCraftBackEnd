package pl.edu.pollub.battleCraft.serviceLayer.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

import java.util.*;

@Component
public class UserAccountValidator implements Validator {
    private Errors errors;

    private final TournamentRepository tournamentRepository;

    private final AddressValidator addressValidator;

    private final UserAccountRepository userAccountRepository;

    public UserAccountValidator(TournamentRepository tournamentRepository, AddressValidator addressValidator, UserAccountRepository userAccountRepository) {
        this.tournamentRepository = tournamentRepository;
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
        UserAccountRequestDTO userAccountRequestDTO = (UserAccountRequestDTO) o;
        this.validateName(userAccountRequestDTO.name);
        this.validateName(userAccountRequestDTO.nameChange);
        this.validateEMail(userAccountRequestDTO.email);
        this.validateFirstName(userAccountRequestDTO.firstname);
        this.validateLastName(userAccountRequestDTO.lastname);
        this.validatePhoneNumber(userAccountRequestDTO.phoneNumber);
        addressValidator.validate(userAccountRequestDTO,errors);
    }

    private void validateName(String name){
        if(name==null || !name.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][a-zzżźćńółęąś]{2,29}$"))
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
        if(!phoneNumber.equals("") || !phoneNumber.matches("^[0-9]{9,11}$"))
            errors.rejectValue("phoneNumber","","Invalid phone number");
    }

    private void validateEMail(String email){
        if(email==null || !email.matches("(^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.+[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@+(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$)"))
            errors.rejectValue("email","","Invalid email");
    }

    public UserAccount getValidatedUserAccountToEdit(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        UserAccount tournamentToEdit = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userAccountRequestDTO.name))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,userAccountRequestDTO.name));
        return tournamentToEdit;
    }

    public Tournament[] getValidatedTournaments(String tournamentsType,List<InvitationDTO> tournaments, BindingResult bindingResult){
        if(tournaments.size()==0)
            return new Tournament[] {};
        if(containsDuplicates(tournaments))
            bindingResult.rejectValue(tournamentsType,"","You cannot have duplicated tournament");
        String[] tournamentNames = tournaments.stream()
                .map(invitation -> invitation.name).toArray(String[]::new);
        List<Tournament> tournamentsList =
                tournamentRepository.findAcceptedTournamentsByUniqueNames(tournamentNames);
        return tournamentsList.toArray(new Tournament[tournamentsList.size()]);
    }

    private boolean containsDuplicates(List<InvitationDTO> values){
        Set<InvitationDTO> setWithoutDuplicates = new HashSet<>(values);
        return setWithoutDuplicates.size() < values.size();
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid user account data", bindingResult);
        }
    }
}
