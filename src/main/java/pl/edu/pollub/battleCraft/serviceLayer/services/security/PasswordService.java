package pl.edu.pollub.battleCraft.serviceLayer.services.security;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.InvalidPasswordException;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security.ChangePasswordDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.EmailDTO;

import java.util.Optional;

@Service
public class PasswordService {
    private final AuthorityRecognizer authorityRecognizer;

    private final UserAccountRepository userAccountRepository;

    private final MailUtil mailUtil;

    public PasswordService(AuthorityRecognizer authorityRecognizer, UserAccountRepository userAccountRepository, MailUtil mailUtil) {
        this.authorityRecognizer = authorityRecognizer;
        this.userAccountRepository = userAccountRepository;
        this.mailUtil = mailUtil;
    }

    @Transactional(rollbackFor = {InvalidPasswordException.class,ThisObjectIsBannedException.class})
    public void changePassword(ChangePasswordDTO changePasswordDTO){
        if(changePasswordDTO.getPassword().length()<8 || changePasswordDTO.getPassword().length() > 32){
            throw new InvalidPasswordException("Password should have more than 8 characters and less than 32");
        }

        if(!changePasswordDTO.getPassword().equals(changePasswordDTO.getPasswordConfirm())){
            throw new InvalidPasswordException("Password confirmation and password are not the same");
        }


        String username = authorityRecognizer.getCurrentUserNameFromContext();
        UserAccount userAccount = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(username))
                .orElseThrow(()-> new ObjectNotFoundException(UserAccount.class,username));

        if(userAccount instanceof Player){
            if((userAccount).isBanned())
                throw new ThisObjectIsBannedException("Account",username);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if(!bCryptPasswordEncoder.matches(changePasswordDTO.getOldPassword(),userAccount.getPassword())){
            throw new InvalidPasswordException("Password incorrect");
        }

        userAccount.setPassword(bCryptPasswordEncoder.encode(changePasswordDTO.getPassword()));
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public void resetPassword(EmailDTO emailDTO){
        UserAccount user = Optional.ofNullable(userAccountRepository.findUserByEmail(emailDTO.getEmail()))
                .orElseThrow(() -> new ObjectNotFoundException(new StringBuilder("Acount with email: ").append(emailDTO.getEmail()).append(" not found").toString()));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String temporaryGeneratedPassword = RandomStringUtils.randomAlphanumeric(10);;
        String recipientAddress = user.getEmail();
        user.setPassword(bCryptPasswordEncoder.encode(temporaryGeneratedPassword));
        userAccountRepository.save(user);
        try {
            mailUtil.sendResetPasswordEmail(recipientAddress,temporaryGeneratedPassword,user.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new VerificationException("There are unrecognized problems with sending verification e-mail. Please try to execute verification process again.");
        }
    }
}
