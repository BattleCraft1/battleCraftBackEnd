package pl.edu.pollub.battleCraft.serviceLayer.services.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.InvalidPasswordConfirmationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.InvalidPasswordException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.ChangePasswordDTO;

import java.util.Optional;

@Service
public class ChangePasswordService {
    private final AuthorityRecognizer authorityRecognizer;
    private final UserAccountRepository userAccountRepository;

    public ChangePasswordService(AuthorityRecognizer authorityRecognizer, UserAccountRepository userAccountRepository) {
        this.authorityRecognizer = authorityRecognizer;
        this.userAccountRepository = userAccountRepository;
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO){
        if(!changePasswordDTO.getPassword().equals(changePasswordDTO.getPasswordConfirm())){
            throw new InvalidPasswordConfirmationException();
        }

        String username = authorityRecognizer.getCurrentUserNameFromContext();
        UserAccount userAccount = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(username))
                .orElseThrow(()-> new ObjectNotFoundException(UserAccount.class,username));

        if(userAccount instanceof Player){
            if(((Player) userAccount).isBanned())
                throw new ThisObjectIsBannedException(UserAccount.class,username);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String newPasswordHash = bCryptPasswordEncoder.encode(changePasswordDTO.getPassword());
        if(!userAccount.getPassword().equals(newPasswordHash)){
            throw new InvalidPasswordException();
        }

        userAccount.setPassword(newPasswordHash);
        userAccountRepository.save(userAccount);
    }
}
