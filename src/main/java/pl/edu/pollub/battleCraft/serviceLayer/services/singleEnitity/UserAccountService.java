package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.builder.UserEditor;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.OrganizationService;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.UserAccountResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.ParticipationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccount.UserAccountWithInvitationsValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation.UserAccountWithParticipationRequestDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService {

    private final UserAccountWithInvitationsValidator userAccountValidator;

    private final UserAccountRepository userAccountRepository;

    private final UserAccountResourcesService userAccountResourcesService;

    private final UserEditor userEditor;

    private final ParticipationService invitationToParticipationService;

    private final OrganizationService invitationToOrganizationService;

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public UserAccountService(UserAccountWithInvitationsValidator userAccountValidator, UserAccountRepository userAccountRepository,
                              UserAccountResourcesService userAccountResourcesService, UserEditor userEditor,
                              ParticipationService invitationToParticipationService,
                              OrganizationService invitationToOrganizationService, AuthorityRecognizer authorityRecognizer) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountValidator = userAccountValidator;
        this.userAccountResourcesService = userAccountResourcesService;
        this.userEditor = userEditor;
        this.invitationToParticipationService = invitationToParticipationService;
        this.invitationToOrganizationService = invitationToOrganizationService;
        this.authorityRecognizer = authorityRecognizer;
    }

    @Transactional(rollbackFor = {EntityValidationException.class, ObjectNotFoundException.class})
    public UserAccount editUserAccount(UserAccountWithParticipationRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        UserAccount userAccountToEdit = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userAccountRequestDTO.getName()))
                .orElseThrow(() -> new ObjectNotFoundException(UserAccount.class,userAccountRequestDTO.getName()));

        authorityRecognizer.checkIfCurrentUserIsOwnerOfAccount(userAccountToEdit);

        userAccountValidator.checkIfUserWithThisNameOrEmailAlreadyExist(userAccountRequestDTO);
        userAccountValidator.validate(userAccountRequestDTO, bindingResult);

        userEditor.edit(userAccountToEdit,
                userAccountRequestDTO.getFirstname(),
                userAccountRequestDTO.getLastname(),
                userAccountRequestDTO.getNameChange(),
                userAccountRequestDTO.getEmail())
                .changeAddress(
                        userAccountRequestDTO.getProvince(),
                        userAccountRequestDTO.getCity(),
                        userAccountRequestDTO.getStreet(),
                        userAccountRequestDTO.getZipCode(),
                        userAccountRequestDTO.getDescription()
                )
                .withPhoneNumber(userAccountRequestDTO.getPhoneNumber())
                .build();

        if(userAccountToEdit instanceof Player){
            Player player = (Player) userAccountToEdit;
            userAccountRequestDTO.setName(player.getName());
            List<ParticipationDTO> participatedTournaments = userAccountValidator.getValidatedParticipation(userAccountRequestDTO);
            invitationToParticipationService.confirmParticipation(player, participatedTournaments);
        }
        if(userAccountToEdit instanceof Organizer){
            Organizer organizer = (Organizer) userAccountToEdit;
            List<ParticipationDTO> organizedTournaments = userAccountValidator.getValidatedOrganization(userAccountRequestDTO.getOrganizedTournaments());
            invitationToOrganizationService.confirmOrganization(organizer, organizedTournaments);
        }

        userAccountValidator.finishValidation(bindingResult);

        if(!userAccountRequestDTO.getName().equals(userAccountRequestDTO.getNameChange()))
        userAccountResourcesService.renameUserAvatar(userAccountRequestDTO.getName(),userAccountRequestDTO.getNameChange());
        return userAccountRepository.save(userAccountToEdit);
    }

    public UserAccount getUserAccount(String userUniqueName) {
        UserAccount userAccount = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userUniqueName))
                .orElseThrow(() -> new ObjectNotFoundException(UserAccount.class,userUniqueName));
        authorityRecognizer.checkIfCurrentUserCanFetchUserAccount(userAccount);
        return userAccount;
    }
}
