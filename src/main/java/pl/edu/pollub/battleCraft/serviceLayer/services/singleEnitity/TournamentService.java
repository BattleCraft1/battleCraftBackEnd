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
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.GroupInvitationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationSender;
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

    private final InvitationSender invitationSender;

    private final GroupInvitationSender groupInvitationSender;


    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, TournamentValidator tournamentValidator, TournamentCreator tournamentCreator, TournamentEditor tournamentEditor, AuthorityRecognizer authorityRecognizer, InvitationSender invitationSender, GroupInvitationSender groupInvitationSender) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentValidator = tournamentValidator;
        this.tournamentCreator = tournamentCreator;
        this.tournamentEditor = tournamentEditor;
        this.authorityRecognizer = authorityRecognizer;
        this.invitationSender = invitationSender;
        this.groupInvitationSender = groupInvitationSender;
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
                        tournamentWebDTO.getToursCount())
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
            groupInvitationSender.inviteParticipantsGroupsList(organizedTournament,groupParticipants);
        }
        else{
            invitationSender.inviteParticipantsList(organizedTournament,participants);
        }

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
                        tournamentWebDTO.getToursCount())
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
            groupInvitationSender.inviteEditedParticipantsGroupsList(tournamentToEdit,groupParticipants);
        }
        else{
            invitationSender.inviteEditedParticipantsList(tournamentToEdit,participants);
        }

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
