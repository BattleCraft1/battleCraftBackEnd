package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.serviceLayer.validators.TournamentValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;

    private final TournamentValidator tournamentValidator;

    private final OrganizerRepository organizerRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository, TournamentValidator tournamentValidator, OrganizerRepository organizerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentValidator = tournamentValidator;
        this.organizerRepository = organizerRepository;
    }

    @Override
    @Transactional
    public TournamentResponseDTO organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        tournamentValidator.checkIfTournamentExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        Tournament organizedTournament = mockOrganizerFromSession
                .startOrganizeTournament(
                        tournamentWebDTO.name,
                        tournamentWebDTO.tablesCount,
                        tournamentWebDTO.playersOnTableCount)
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

        return new TournamentResponseDTO(this.tournamentRepository.save(organizedTournament));
    }

    @Override
    @Transactional
    public TournamentResponseDTO editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        //TO DO: check if this organizer is organizer of this tournament

        Tournament tournamentToEdit = tournamentValidator.getValidatedTournamentToEdit(tournamentWebDTO, bindingResult);

        tournamentValidator.checkIfTournamentToEditExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        mockOrganizerFromSession
                .editOrganizedTournament(
                        tournamentToEdit,
                        tournamentWebDTO.nameChange,
                        tournamentWebDTO.tablesCount,
                        tournamentWebDTO.playersOnTableCount)
                .editOrganizers(organizers)
                .changeAddressForTournament(
                        tournamentWebDTO.province,
                        tournamentWebDTO.city,
                        tournamentWebDTO.street,
                        tournamentWebDTO.zipCode,
                        tournamentWebDTO.description)
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.dateOfStart)
                .endingIn(tournamentWebDTO.dateOfEnd)
                .editParticipants(participants)
                .finishOrganize();

        return new TournamentResponseDTO(this.tournamentRepository.save(tournamentToEdit));
    }

    @Override
    public TournamentResponseDTO getTournament(String tournamentUniqueName) {
        Tournament tournamentToShow = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentUniqueName));

        return new TournamentResponseDTO(tournamentToShow);
    }
}
