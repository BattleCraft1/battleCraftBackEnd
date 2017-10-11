package pl.edu.pollub.battleCraft.serviceLayer.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Province;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament.TournamentWebDTO;

@Component
public class AddressValidator implements Validator {
    private Errors errors;

    @Override
    public boolean supports(Class<?> aClass) {
        return TournamentWebDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        TournamentWebDTO tournamentWebDTO = (TournamentWebDTO) o;
        this.validateProvince(tournamentWebDTO.province);
        this.validateCity(tournamentWebDTO.city);
        this.validateStreet(tournamentWebDTO.street);
        this.validateZipCode(tournamentWebDTO.zipCode);
        this.validateDescription(tournamentWebDTO.description);
    }

    private void validateProvince(String province){
        if(!Province.getNames().contains(province))
            errors.rejectValue("province","Invalid province name");
    }

    private void validateCity(String city){
        if(!city.matches("^[A-Z][a-z0-9]{39}$"))
            errors.rejectValue("city","City must start with big letter and have between 1 and 40 chars");
    }

    private void validateStreet(String street){
        if(!street.matches("^[0-9a-zA-Z. ]{39}$"))
            errors.rejectValue("street","Street and have between 1 and 40 chars");
    }

    private void validateZipCode(String zipCode){
        if(!zipCode.matches("^\\d{2}-\\d{3}$"))
            errors.rejectValue("zipCode","Zip code have invalid format");
    }

    private void validateDescription(String description){
        if(description.length()>100)
            errors.rejectValue("description","Description can have only 100 chars");
    }
}
