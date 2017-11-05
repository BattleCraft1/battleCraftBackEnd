package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.GameService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.implementations.GameValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;

import java.util.Optional;

@Service
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    private final GameValidator gameValidator;

    private final OrganizerRepository organizerRepository;

    private final GameResourcesService gameResourcesService;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, GameValidator gameValidator, OrganizerRepository organizerRepository, GameResourcesService gameResourcesService) {
        this.gameRepository = gameRepository;
        this.gameValidator = gameValidator;
        this.organizerRepository = organizerRepository;
        this.gameResourcesService = gameResourcesService;
    }

    @Override
    @Transactional(rollbackFor = {EntityValidationException.class,EntityNotFoundException.class})
    public GameResponseDTO addGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        gameValidator.checkIfGameExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        Game createdGame = mockOrganizerFromSession.createGame(gameRequestDTO.name);

        return new GameResponseDTO(this.gameRepository.save(createdGame));
    }

    @Override
    @Transactional(rollbackFor = {EntityValidationException.class,EntityNotFoundException.class})
    public GameResponseDTO editGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        //TO DO: check if this organizer is creator of this game

        Game gameToEdit = gameValidator.getValidatedGameToEdit(gameRequestDTO, bindingResult);

        gameValidator.checkIfGameToEditExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        Game editedGame = mockOrganizerFromSession.editGame(gameToEdit,gameRequestDTO.nameChange);

        if(!gameRequestDTO.name.equals(gameRequestDTO.nameChange))
        gameResourcesService.renameGamesRules(gameRequestDTO.name,gameRequestDTO.nameChange);

        return new GameResponseDTO(this.gameRepository.save(editedGame));
    }

    @Override
    public GameResponseDTO getGame(String gameUniqueName) {
        Game gameToShow = Optional.ofNullable(gameRepository.findNotBannedGameByUniqueName(gameUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(Game.class,gameUniqueName));

        return new GameResponseDTO(gameToShow);
    }
}
