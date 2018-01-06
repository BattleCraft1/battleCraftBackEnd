package pl.edu.pollub.battleCraft.dataLayer.domain.User.mappers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

@Component
public class RegistrationDTOToUserAccountMapper {

    public UserAccount mapToUser(RegistrationDTO registrationDTO){

        UserAccount userAccount = new UserAccount();

        this.completeUserData(userAccount,registrationDTO);

        return userAccount;
    }

    public Administrator mapToAdmin(RegistrationDTO registrationDTO){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Administrator administrator = new Administrator();

        this.completeUserData(administrator,registrationDTO);

        return administrator;
    }

    private void completeUserData(UserAccount userAccount,RegistrationDTO registrationDTO){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

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

        userAccount.getAddressOwnership().insertAddress(address);
    }
}
