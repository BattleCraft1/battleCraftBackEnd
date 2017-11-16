package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.builder.UserEditor;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToOrganizationService;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToParticipationService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.UserAccountResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.InvitationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccountValidator;
import pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.UserAccountToResponseDTOMapper;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService {

    private final UserAccountValidator userAccountValidator;

    private final UserAccountRepository userAccountRepository;

    private final UserAccountResourcesService userAccountResourcesService;

    private final UserEditor userEditor;

    private final UserAccountToResponseDTOMapper userAccountToResponseDTOMapper;

    private final InvitationToParticipationService invitationToParticipationService;

    private final InvitationToOrganizationService invitationToOrganizationService;

    @Autowired
    public UserAccountService(UserAccountValidator userAccountValidator, UserAccountRepository userAccountRepository,
                              UserAccountResourcesService userAccountResourcesService, UserEditor userEditor,
                              UserAccountToResponseDTOMapper userAccountToResponseDTOMapper, InvitationToParticipationService invitationToParticipationService,
                              InvitationToOrganizationService invitationToOrganizationService) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountValidator = userAccountValidator;
        this.userAccountResourcesService = userAccountResourcesService;
        this.userEditor = userEditor;
        this.userAccountToResponseDTOMapper = userAccountToResponseDTOMapper;
        this.invitationToParticipationService = invitationToParticipationService;
        this.invitationToOrganizationService = invitationToOrganizationService;
    }

    @Transactional(rollbackFor = {EntityValidationException.class, EntityNotFoundException.class})
    public UserAccountResponseDTO editUserAccount(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        UserAccount userAccountToEdit = userAccountValidator.getValidatedUserAccountToEdit(userAccountRequestDTO, bindingResult);

        userAccountValidator.checkIfUserWithThisNameAlreadyExist(userAccountRequestDTO,bindingResult);
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
            List<InvitationDTO> participatedTournaments = userAccountValidator.getValidatedPlayersInvitations(userAccountRequestDTO, bindingResult);
            invitationToParticipationService.sendInvitations(player, participatedTournaments);
        }
        if(userAccountToEdit instanceof Organizer){
            Organizer organizer = (Organizer) userAccountToEdit;
            List<InvitationDTO> organizedTournaments = userAccountValidator.getValidatedOrganizersInvitations(
                            userAccountRequestDTO.getOrganizedTournaments(), bindingResult);
            invitationToOrganizationService.sendInvitations(organizer, organizedTournaments);
        }

        userAccountValidator.finishValidation(bindingResult);

        if(!userAccountRequestDTO.getName().equals(userAccountRequestDTO.getNameChange()))
        userAccountResourcesService.renameUserAvatar(userAccountRequestDTO.getName(),userAccountRequestDTO.getNameChange());
        UserAccount account = userAccountRepository.save(userAccountToEdit);
        return userAccountToResponseDTOMapper.map(account);
    }

    public UserAccountResponseDTO getUserAccount(String userUniqueName) {
        UserAccount userAccountToShow = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(userUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,userUniqueName));

        return userAccountToResponseDTOMapper.map(userAccountToShow);
    }
}