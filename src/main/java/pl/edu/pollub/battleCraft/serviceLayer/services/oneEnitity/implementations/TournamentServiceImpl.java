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
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.serviceLayer.validators.TournamentOrganizationValidator;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament.TournamentWebDTO;

import java.util.List;

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
    public void organizeTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        tournamentOrganizationValidator.validate(tournamentWebDTO,bindingResult);

        Game tournamentGame = gameRepository.findAcceptedGameByName(tournamentWebDTO.game);
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(tournamentWebDTO.participants);
        Player[] participants = participantsList.toArray(new Player[participantsList.size()]);
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.organizers);
        Organizer[] organizers = organizersList.toArray(new Organizer[organizersList.size()]);

        tournamentOrganizationValidator.validateDataFromDatabase(tournamentGame,participants,organizers,bindingResult,tournamentWebDTO);

        Tournament organizerdTournament = mockOrganizerFromSession
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
                        tournamentWebDTO.description
                ))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.dateOfStart)
                .endingIn(tournamentWebDTO.dateOfEnd)
                .inviteParticipants(participants)
                .finishOrganize();

        tournamentRepository.save(organizerdTournament);
    }

    @Override
    public void editTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult) {

    }

    @Override
    public TournamentWebDTO getTournament(String tournamentUniqueName) {
        return null;
    }

    @Override
    public void deleteOrganizatorFromTournament(String organizatorName) {

    }

    @Override
    public void deleteParticipantFromTournament(String participantName) {

    }
}
