package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserBuilder;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.UserAccountService;
import pl.edu.pollub.battleCraft.serviceLayer.validators.UserAccountValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService{

    private final UserAccountValidator userAccountValidator;

    private final UserAccountRepository userAccountRepository;

    private UserBuilder userBuilder = new UserBuilder();

    @Autowired
    public UserAccountServiceImpl(UserAccountValidator userAccountValidator, UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountValidator = userAccountValidator;
    }

    @Override
    public UserAccountResponseDTO editUserAccount(UserAccountRequestDTO userAccountRequestDTO, BindingResult bindingResult) {
        UserAccount userAccountToEdit = userAccountValidator.getValidatedUserAccountToEdit(userAccountRequestDTO, bindingResult);
        userAccountValidator.validate(userAccountRequestDTO, bindingResult);

        userBuilder.edit(userAccountToEdit,
                userAccountRequestDTO.firstname,
                userAccountRequestDTO.lastname,
                userAccountRequestDTO.name,
                userAccountRequestDTO.email)
                .changeAddress(
                        userAccountRequestDTO.province,
                        userAccountRequestDTO.city,
                        userAccountRequestDTO.street,
                        userAccountRequestDTO.zipCode,
                        userAccountRequestDTO.description
                )
                .withPhoneNumber(userAccountRequestDTO.phoneNumber)
                .build();

        if(userAccountToEdit instanceof Player){
            Player player = (Player) userAccountToEdit;
            Tournament[] participatedTournaments = userAccountValidator.getValidatedTournaments(userAccountRequestDTO.participatedTournaments, bindingResult);
            player.editParticipation(participatedTournaments);
        }
        if(userAccountToEdit instanceof Organizer){
            Organizer organizer = (Organizer) userAccountToEdit;
            Tournament[] organizedTournaments = userAccountValidator.getValidatedTournaments(userAccountRequestDTO.organizedTournaments, bindingResult);
            organizer.editOrganizations(organizedTournaments);
        }

        userAccountValidator.finishValidation(bindingResult);

        return new UserAccountResponseDTO(userAccountRepository.save(userAccountToEdit));
    }

    @Override
    public UserAccountResponseDTO getUserAccount(String userUniqueName) {
        UserAccount userAccountToShow = Optional.ofNullable(userAccountRepository.findUserAccountToEditByUniqueName(userUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,userUniqueName));

        return new UserAccountResponseDTO(userAccountToShow);
    }
}
