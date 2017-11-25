package pl.edu.pollub.battleCraft.serviceLayer.services.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.mappers.RegistrationDTOToUserAccountMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.mappers.UserAccountToPlayerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.events.OnRegistrationCompleteEvent;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.VerificationTokenUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.RegistrationValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration.RegistrationDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class RegistrationService {

    private final RegistrationValidator registrationValidator;

    private final UserAccountRepository userAccountRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final RegistrationDTOToUserAccountMapper registrationDTOToUserAccountMapper;

    private final VerificationTokenUtil verificationTokenUtil;

    private final MailUtil mailUtil;

    private final UserAccountToPlayerMapper userAccountToPlayerMapper;

    public RegistrationService(RegistrationValidator registrationValidator, UserAccountRepository userAccountRepository, ApplicationEventPublisher eventPublisher, RegistrationDTOToUserAccountMapper registrationDTOToUserAccountMapper, VerificationTokenUtil verificationTokenUtil, MailUtil mailUtil, UserAccountToPlayerMapper userAccountToPlayerMapper) {
        this.registrationValidator = registrationValidator;
        this.userAccountRepository = userAccountRepository;
        this.eventPublisher = eventPublisher;
        this.registrationDTOToUserAccountMapper = registrationDTOToUserAccountMapper;
        this.verificationTokenUtil = verificationTokenUtil;
        this.mailUtil = mailUtil;
        this.userAccountToPlayerMapper = userAccountToPlayerMapper;
    }

    public void register(RegistrationDTO registrationDTO, BindingResult bindingResult, HttpServletRequest request){
        registrationValidator.validate(registrationDTO, bindingResult);
        registrationValidator.checkIfUserWithThisNameAlreadyExist(registrationDTO,bindingResult);
        registrationValidator.finishValidation(bindingResult);
        this.saveNewUserAccount(registrationDTO,request);
    }

    @Transactional
    void saveNewUserAccount(RegistrationDTO registrationDTO, HttpServletRequest request){
        UserAccount userAccount = registrationDTOToUserAccountMapper.map(registrationDTO);
        userAccountRepository.save(userAccount);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userAccount, request.getLocale(), request.getContextPath()));
    }

    @Transactional
    public void resendToken(String name, HttpServletRequest request){
        UserAccount user= userAccountRepository.findNotAcceptedUserByUniqueName(name);
        VerificationToken newToken=verificationTokenUtil.generateNewVerificationToken(user);
        String recipientAddress = user.getEmail();
        String appUrl = request.getContextPath();

        mailUtil.sendVerificationMail(recipientAddress,appUrl,newToken.getToken());
    }

    @Transactional
    public void confirmRegistration(String token){

        VerificationToken verificationToken = Optional.ofNullable(verificationTokenUtil.getTokenObject(token))
                .orElseThrow(() -> new VerificationException("Your activation token is invalid, please try to resend it"));

        UserAccount user = verificationToken.getUser();

        userAccountRepository.delete(user);
        Player player = userAccountToPlayerMapper.map(user);
        userAccountRepository.save(player);
    }
}
