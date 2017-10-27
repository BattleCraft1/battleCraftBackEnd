package pl.edu.pollub.battleCraft.serviceLayer.validators.implementations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Province;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

@Component
public class AddressValidator implements Validator {
    private Errors errors;

    @Override
    public boolean supports(Class<?> aClass) {
        return AddressOwnerRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        AddressOwnerRequestDTO addressOwnerRequestDTO = (AddressOwnerRequestDTO) o;
        this.validateProvince(addressOwnerRequestDTO.province);
        this.validateCity(addressOwnerRequestDTO.city);
        this.validateStreet(addressOwnerRequestDTO.street);
        this.validateZipCode(addressOwnerRequestDTO.zipCode);
        this.validateDescription(addressOwnerRequestDTO.description);
    }

    private void validateProvince(String province){
        if(!Province.getNames().contains(province))
            errors.rejectValue("province","","Invalid province name");
    }

    private void validateCity(String city){
        if(!city.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźż]{1,39}$"))
            errors.rejectValue("city","","City must start with big letter and have between 2 and 40 chars");
    }

    private void validateStreet(String street){
        if(!street.matches("^[0-9A-ZĄĆĘŁŃÓŚŹŻ][\\sA-ZĄĆĘŁŃÓŚŹŻ0-9a-ząćęłńóśźż. ]{1,39}$"))
            errors.rejectValue("street","","Street must have between 2 and 40 chars");
    }

    private void validateZipCode(String zipCode){
        if(!zipCode.matches("^\\d{2}-\\d{3}$"))
            errors.rejectValue("zipCode","","Zip code have invalid format");
    }

    private void validateDescription(String description){
        if(description.length()>100)
            errors.rejectValue("description","","Description can have only 100 chars");
    }
}
