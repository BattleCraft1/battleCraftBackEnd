package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.serviceLayer.validators.TournamentOrganizationValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService{

    private final OrganizerRepository organizerRepository;

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    private final TournamentRepository tournamentRepository;

    private final TournamentOrganizationValidator tournamentOrganizationValidator;

    @Autowired
    public TournamentServiceImpl(OrganizerRepository organizerRepository, GameRepository gameRepository, PlayerRepository playerRepository, TournamentRepository tournamentRepository, TournamentOrganizationValidator tournamentOrganizationValidator) {
        this.organizerRepository = organizerRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentOrganizationValidator = tournamentOrganizationValidator;
    }

    @Override
    public TournamentResponseDTO organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.name);
        if(tournamentExist!=null)
            bindingResult.rejectValue("nameChange","","Tournament with this name already exist.");

        Game tournamentGame = gameRepository.findAcceptedGameByName(tournamentWebDTO.game);
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(tournamentWebDTO.participants);
        Player[] participants = participantsList.toArray(new Player[participantsList.size()]);
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.organizers);
        Organizer[] organizers = organizersList.toArray(new Organizer[organizersList.size()]);

        tournamentOrganizationValidator.validate(tournamentWebDTO,bindingResult);
        tournamentOrganizationValidator.validateDataFromDatabase(tournamentGame,participants,organizers,bindingResult,tournamentWebDTO);

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

        return new TournamentResponseDTO(tournamentRepository.save(organizedTournament));
    }

    @Override
    public TournamentResponseDTO editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) {
        Tournament tournamentToEdit = Optional.ofNullable(tournamentRepository.findByName(tournamentWebDTO.name))
                        .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentWebDTO.name));

        Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.nameChange);
        if(tournamentExist!=null)
            bindingResult.rejectValue("nameChange","","Tournament with this name already exist.");

        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        //TO DO: check if this organizer is organizer of this tournament

        Game tournamentGame = gameRepository.findAcceptedGameByName(tournamentWebDTO.game);
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(tournamentWebDTO.participants);
        Player[] participants = participantsList.toArray(new Player[participantsList.size()]);
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.organizers);
        Organizer[] organizers = organizersList.toArray(new Organizer[organizersList.size()]);

        tournamentOrganizationValidator.validate(tournamentWebDTO,bindingResult);
        tournamentOrganizationValidator.validateDataToEditFromDatabase(tournamentToEdit,tournamentGame,participants,organizers,bindingResult,tournamentWebDTO);

        Tournament organizedTournament = mockOrganizerFromSession
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

        return new TournamentResponseDTO(tournamentRepository.save(organizedTournament));
    }

    @Override
    public TournamentResponseDTO getTournament(String tournamentUniqueName) {
        Tournament tournamentToShow = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentUniqueName));

        return new TournamentResponseDTO(tournamentToShow);
    }
}
