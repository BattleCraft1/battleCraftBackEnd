package pl.edu.pollub.battleCraft.serviceLayer.services.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.AdminRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.mappers.RegistrationDTOToUserAccountMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.mappers.UserAccountToPlayerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.events.OnRegistrationCompleteEvent;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.VerificationTokenUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccount.RegistrationValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.EmailDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

import java.util.Optional;

@Service
public class RegistrationService {

    private final RegistrationValidator registrationValidator;

    private final UserAccountRepository userAccountRepository;

    private final AdminRepository adminRepository;

    private final PlayerRepository playerRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final RegistrationDTOToUserAccountMapper registrationDTOToUserAccountMapper;

    private final VerificationTokenUtil verificationTokenUtil;

    private final MailUtil mailUtil;

    private final UserAccountToPlayerMapper userAccountToPlayerMapper;

    @Autowired
    public RegistrationService(RegistrationValidator registrationValidator, UserAccountRepository userAccountRepository, AdminRepository adminRepository, PlayerRepository playerRepository, ApplicationEventPublisher eventPublisher, RegistrationDTOToUserAccountMapper registrationDTOToUserAccountMapper, VerificationTokenUtil verificationTokenUtil, MailUtil mailUtil, UserAccountToPlayerMapper userAccountToPlayerMapper) {
        this.registrationValidator = registrationValidator;
        this.userAccountRepository = userAccountRepository;
        this.adminRepository = adminRepository;
        this.playerRepository = playerRepository;
        this.eventPublisher = eventPublisher;
        this.registrationDTOToUserAccountMapper = registrationDTOToUserAccountMapper;
        this.verificationTokenUtil = verificationTokenUtil;
        this.mailUtil = mailUtil;
        this.userAccountToPlayerMapper = userAccountToPlayerMapper;
    }

    @Transactional
    public void register(RegistrationDTO registrationDTO, BindingResult bindingResult){
        registrationValidator.validate(registrationDTO, bindingResult);
        registrationValidator.checkIfUserWithThisNameAlreadyExist(registrationDTO,bindingResult);
        registrationValidator.finishValidation(bindingResult);
        this.saveNewUserAccount(registrationDTO);
    }

    @Transactional
    public void createAdminAccount(RegistrationDTO registrationDTO, BindingResult bindingResult){
        registrationValidator.validate(registrationDTO, bindingResult);
        registrationValidator.checkIfUserWithThisNameAlreadyExist(registrationDTO,bindingResult);
        registrationValidator.finishValidation(bindingResult);
        Administrator admin = registrationDTOToUserAccountMapper.mapToAdmin(registrationDTO);
        adminRepository.save(admin);
    }

    @Transactional
    void saveNewUserAccount(RegistrationDTO registrationDTO){
        UserAccount userAccount = registrationDTOToUserAccountMapper.mapToUser(registrationDTO);
        userAccountRepository.save(userAccount);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userAccount));
    }

    @Transactional
    public void resendToken(EmailDTO emailResendDTO){
        UserAccount user= Optional.ofNullable(userAccountRepository.findNotAcceptedUserByEmail(emailResendDTO.getEmail()))
                .orElseThrow(() -> new ObjectNotFoundException(new StringBuilder("Acount with email: ").append(emailResendDTO.getEmail()).append(" not found").toString()));
        if(user instanceof Player)
            throw new ObjectNotFoundException(new StringBuilder("Acount with email: ").append(emailResendDTO.getEmail()).append(" not found").toString());
        VerificationToken newToken=verificationTokenUtil.generateNewVerificationToken(user);
        String recipientAddress = user.getEmail();

        try {
            mailUtil.sendVerificationMail(recipientAddress,newToken.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            throw new VerificationException("There are unrecognized problems with sending verification e-mail. Please try to execute verification process again.");
        }
    }

    @Transactional
    public void confirmRegistration(String token){

        VerificationToken verificationToken = Optional.ofNullable(verificationTokenUtil.getTokenObject(token))
                .orElseThrow(() -> new VerificationException("Your activation token is invalid, please try to resend it"));

        verificationTokenUtil.checkIfTokenIsExpired(verificationToken);

        UserAccount user = verificationToken.getUser();

        verificationTokenUtil.removeToken(verificationToken);
        userAccountRepository.delete(user);
        Player player = userAccountToPlayerMapper.map(user);
        playerRepository.save(player);

        try {
            mailUtil.sendRegistrationCompleteMail(player.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            throw new VerificationException("There are unrecognized problems with sending verification e-mail. Please try to execute verification process again.");
        }
    }
}
