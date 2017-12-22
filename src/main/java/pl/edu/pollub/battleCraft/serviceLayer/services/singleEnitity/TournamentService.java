package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder.TournamentCreator;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder.TournamentEditor;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToGroupParticipationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToOrganizationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToParticipationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.TournamentValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.DuelTournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.GroupTournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    private final TournamentValidator tournamentValidator;

    private final TournamentCreator tournamentCreator;

    private final TournamentEditor tournamentEditor;

    private final AuthorityRecognizer authorityRecognizer;

    private final InvitationToParticipationSender invitationToParticipationSender;

    private final InvitationToGroupParticipationSender invitationToGroupParticipationSender;

    private final InvitationToOrganizationSender invitationToOrganizationSender;


    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, TournamentValidator tournamentValidator, TournamentCreator tournamentCreator, TournamentEditor tournamentEditor, AuthorityRecognizer authorityRecognizer, InvitationToParticipationSender invitationSender, InvitationToGroupParticipationSender groupInvitationSender, InvitationToOrganizationSender invitationToOrganizationSender) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentValidator = tournamentValidator;
        this.tournamentCreator = tournamentCreator;
        this.tournamentEditor = tournamentEditor;
        this.authorityRecognizer = authorityRecognizer;
        this.invitationToParticipationSender = invitationSender;
        this.invitationToGroupParticipationSender = groupInvitationSender;
        this.invitationToOrganizationSender = invitationToOrganizationSender;
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Tournament organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {

        tournamentValidator.checkIfTournamentExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        String currentUserName = authorityRecognizer.getCurrentUserNameFromContext();
        tournamentWebDTO.getOrganizers().add(currentUserName);
        List<Organizer> organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);

        List<List<Player>> groupParticipants = new ArrayList<>();
        List<Player> participants = new ArrayList<>();

        if(tournamentWebDTO instanceof GroupTournamentRequestDTO){
            groupParticipants = tournamentValidator.getValidatedParticipantsGroups((GroupTournamentRequestDTO) tournamentWebDTO,bindingResult);
        }
        else{
            participants = tournamentValidator.getValidatedParticipants((DuelTournamentRequestDTO) tournamentWebDTO,bindingResult);
        }

        tournamentValidator.finishValidation(bindingResult);

        Tournament organizedTournament = tournamentCreator.startOrganizeTournament(
                        tournamentWebDTO.getName(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getTournamentType(),
                        tournamentWebDTO.getTurnsCount())
                .in(new Address(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription()))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .finishOrganize();

        if(organizedTournament instanceof GroupTournament){
            invitationToGroupParticipationSender.inviteParticipantsGroupsList(organizedTournament,groupParticipants);
        }
        else{
            invitationToParticipationSender.inviteParticipantsList(organizedTournament,participants);
        }

        invitationToOrganizationSender.inviteOrganizersList(organizedTournament,organizers);

        return this.tournamentRepository.save(organizedTournament);
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Tournament editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult){

        Tournament tournamentToEdit = Optional.ofNullable(tournamentRepository.findByName(tournamentWebDTO.getName()))
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentWebDTO.getName()));

        authorityRecognizer.checkIfCurrentUserCanEditTournament(tournamentToEdit);

        tournamentValidator.checkIfTournamentWithThisNameAlreadyExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        List<Organizer> organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);

        List<List<Player>> groupParticipants = new ArrayList<>();
        List<Player> participants = new ArrayList<>();

        if(tournamentWebDTO instanceof GroupTournamentRequestDTO){
            groupParticipants = tournamentValidator.getValidatedParticipantsGroups((GroupTournamentRequestDTO) tournamentWebDTO,bindingResult);
        }
        else{
            participants = tournamentValidator.getValidatedParticipants((DuelTournamentRequestDTO) tournamentWebDTO,bindingResult);
        }

        tournamentValidator.finishValidation(bindingResult);

        tournamentEditor.editOrganizedTournament(
                        tournamentToEdit,
                        tournamentWebDTO.getNameChange(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getTournamentType(),
                        tournamentWebDTO.getTurnsCount())
                .changeAddress(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription())
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .finishEditing();

        if(tournamentToEdit instanceof GroupTournament){
            invitationToGroupParticipationSender.inviteEditedParticipantsGroupsList(tournamentToEdit,groupParticipants);
        }
        else{
            invitationToParticipationSender.inviteEditedParticipantsList(tournamentToEdit,participants);
        }

        invitationToOrganizationSender.inviteOrganizersList(tournamentToEdit,organizers);

        authorityRecognizer.checkIfEditedTournamentNeedReAcceptation(tournamentToEdit);

        return this.tournamentRepository.save(tournamentToEdit);
    }

    public Tournament getTournament(String tournamentUniqueName) {
        Tournament tournament = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentUniqueName));
        authorityRecognizer.checkIfCurrentUserCanFetchTournament(tournament);
        return tournament;
    }
}
