package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
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

    public UserAccount getValidatedUserAccountToEdit(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        UserAccount tournamentToEdit = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userAccountRequestDTO.getName()))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,userAccountRequestDTO.getName()));
        return tournamentToEdit;
    }

    public Tournament[] getValidatedTournaments(String tournamentsType,List<InvitationDTO> tournaments, BindingResult bindingResult){
        if(tournaments.size()==0)
            return new Tournament[] {};
        if(containsDuplicates(tournaments))
            bindingResult.rejectValue(tournamentsType,"","You cannot have duplicated tournament");
        String[] tournamentNames = tournaments.stream()
                .map(invitation -> invitation.getName()).toArray(String[]::new);
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
