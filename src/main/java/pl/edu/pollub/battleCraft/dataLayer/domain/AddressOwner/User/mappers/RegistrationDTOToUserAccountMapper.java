package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.mappers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

@Component
public class RegistrationDTOToUserAccountMapper {

    public UserAccount map(RegistrationDTO registrationDTO){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        UserAccount userAccount = new UserAccount();
        userAccount.setFirstname(registrationDTO.getFirstname());
        userAccount.setLastname(registrationDTO.getLastname());
        userAccount.setName(registrationDTO.getNameChange());
        userAccount.setEmail(registrationDTO.getEmail());
        userAccount.setPassword(registrationDTO.getPassword());
        userAccount.setPhoneNumber(registrationDTO.getPhoneNumber());
        userAccount.setPassword(bCryptPasswordEncoder.encode(registrationDTO.getPassword()));

        Address address = new Address(
                registrationDTO.getProvince(),
                registrationDTO.getCity(),
                registrationDTO.getStreet(),
                registrationDTO.getZipCode(),
                registrationDTO.getDescription()
        );

        userAccount.initAddress(address);

        return userAccount;
    }
}
