package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.serviceLayer.validators.TournamentOrganizationValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;

    private final TournamentOrganizationValidator tournamentOrganizationValidator;

    private final OrganizerRepository organizerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository, TournamentOrganizationValidator tournamentOrganizationValidator, OrganizerRepository organizerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentOrganizationValidator = tournamentOrganizationValidator;
        this.organizerRepository = organizerRepository;
    }

    @Override
    @Transactional
    public TournamentResponseDTO organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        tournamentOrganizationValidator.checkIfTournamentExist(tournamentWebDTO,bindingResult);
        tournamentOrganizationValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentOrganizationValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentOrganizationValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentOrganizationValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);
        tournamentOrganizationValidator.finishValidation(bindingResult);

        Tournament organizedTournament = mockOrganizerFromSession
                .startOrganizeTournament(
                        tournamentWebDTO.name,
                        tournamentWebDTO.tablesCount,
                        tournamentWebDTO.maxPlayers)
                .with(organizers)
                .in(new Address(
                        tournamentWebDTO.province,
                        tournamentWebDTO.city,
                        tournamentWebDTO.street,
                        tournamentWebDTO.zipCode,
                        tournamentWebDTO.description))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.dateOfStart)
                .endingIn(tournamentWebDTO.dateOfEnd)
                .inviteParticipants(participants)
                .finishOrganize();

        Session hibernateSession = (Session) entityManager.getDelegate();
        hibernateSession.save(organizedTournament);
        return new TournamentResponseDTO(organizedTournament);
    }

    @Override
    @Transactional
    public TournamentResponseDTO editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        //TO DO: check if this organizer is organizer of this tournament

        Tournament tournamentToEdit = tournamentOrganizationValidator.getValidatedTournamentToEdit(tournamentWebDTO, bindingResult);

        tournamentOrganizationValidator.checkIfTournamentToEditExist(tournamentWebDTO,bindingResult);
        tournamentOrganizationValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentOrganizationValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentOrganizationValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentOrganizationValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        mockOrganizerFromSession
                .editOrganizedTournament(
                        tournamentToEdit,
                        tournamentWebDTO.nameChange,
                        tournamentWebDTO.tablesCount,
                        tournamentWebDTO.maxPlayers)
                .editOrganizers(organizers)
                .in(new Address(
                        tournamentWebDTO.province,
                        tournamentWebDTO.city,
                        tournamentWebDTO.street,
                        tournamentWebDTO.zipCode,
                        tournamentWebDTO.description))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.dateOfStart)
                .endingIn(tournamentWebDTO.dateOfEnd)
                .editParticipants(participants)
                .finishOrganize();

        tournamentOrganizationValidator.validateWithCurrentOrganizers(bindingResult,tournamentToEdit.getOrganizers());
        tournamentOrganizationValidator.validateWithCurrentParticipants(tournamentWebDTO,bindingResult,tournamentToEdit.getParticipants());
        tournamentOrganizationValidator.finishValidation(bindingResult);

        Session hibernateSession = (Session) entityManager.getDelegate();
        hibernateSession.saveOrUpdate(tournamentToEdit);
        return new TournamentResponseDTO(tournamentToEdit);
    }

    @Override
    public TournamentResponseDTO getTournament(String tournamentUniqueName) {
        Tournament tournamentToShow = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentUniqueName));

        return new TournamentResponseDTO(tournamentToShow);
    }
}
