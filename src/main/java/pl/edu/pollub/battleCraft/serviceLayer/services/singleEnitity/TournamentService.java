package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder.TournamentCreator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder.TournamentEditor;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.TournamentValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    private final TournamentValidator tournamentValidator;

    private final TournamentCreator tournamentCreator;

    private final TournamentEditor tournamentEditor;

    private final AuthorityRecognizer authorityRecognizer;


    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, TournamentValidator tournamentValidator, TournamentCreator tournamentCreator, TournamentEditor tournamentEditor, AuthorityRecognizer authorityRecognizer) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentValidator = tournamentValidator;
        this.tournamentCreator = tournamentCreator;
        this.tournamentEditor = tournamentEditor;
        this.authorityRecognizer = authorityRecognizer;
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Tournament organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {

        tournamentValidator.checkIfTournamentExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        List<Organizer> organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        List<List<Player>> participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        Tournament organizedTournament = tournamentCreator
                .startOrganizeTournament(
                        tournamentWebDTO.getName(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getTournamentType(),
                        tournamentWebDTO.getToursCount())
                .with(organizers)
                .in(new Address(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription()))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .inviteParticipants(participants)
                .finishOrganize();

        return this.tournamentRepository.save(organizedTournament);
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Tournament editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult){

        Tournament tournamentToEdit = tournamentValidator.getValidatedTournamentToEdit(tournamentWebDTO, bindingResult);

        this.checkIfCurrentUserIsOrganizerOfTournament(tournamentToEdit);

        tournamentValidator.checkIfTournamentWithThisNameAlreadyExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        List<Organizer> organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        List<List<Player>> participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        tournamentEditor.editOrganizedTournament(
                        tournamentToEdit,
                        tournamentWebDTO.getNameChange(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getTournamentType(),
                        tournamentWebDTO.getToursCount())
                .editOrganizers(organizers)
                .changeAddress(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription())
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .editParticipants(participants)
                .finishEditing();

        this.checkIfEditedTournamentNeedReAcceptation(tournamentToEdit);

        return this.tournamentRepository.save(tournamentToEdit);
    }

    public Tournament getTournament(String tournamentUniqueName) {
        return Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentUniqueName));
    }

    private void checkIfCurrentUserIsOrganizerOfTournament(Tournament tournament){
        String role = authorityRecognizer.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            List<String> organizersNames = tournament.getOrganizations().stream()
                    .map(organization -> organization.getOrganizer().getName())
                    .collect(Collectors.toList());
            if(!organizersNames.contains(authorityRecognizer.getCurrentUserNameFromContext()))
                throw new YouAreNotOwnerOfThisObjectException(Tournament.class,tournament.getName());
        }
    }

    private void checkIfEditedTournamentNeedReAcceptation(Tournament tournament){
        String role = authorityRecognizer.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            tournament.setStatus(TournamentStatus.NEW);
        }
    }
}
