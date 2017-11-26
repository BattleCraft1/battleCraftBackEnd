package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.GameResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.GameValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;

import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final GameValidator gameValidator;

    private final OrganizerRepository organizerRepository;

    private final GameResourcesService gameResourcesService;

    @Autowired
    public GameService(GameRepository gameRepository, GameValidator gameValidator, OrganizerRepository organizerRepository, GameResourcesService gameResourcesService) {
        this.gameRepository = gameRepository;
        this.gameValidator = gameValidator;
        this.organizerRepository = organizerRepository;
        this.gameResourcesService = gameResourcesService;
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Game addGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {
        Organizer mockOrganizerFromSession = organizerRepository.findByName("dept2123");

        gameValidator.checkIfGameExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        Game createdGame = new Game(gameRequestDTO.getName(),mockOrganizerFromSession);

        return this.gameRepository.save(createdGame);
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Game editGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {

        //TO DO: check if this organizer is creator of this game

        Game gameToEdit = gameValidator.getValidatedGameToEdit(gameRequestDTO, bindingResult);

        gameValidator.checkIfGameToEditExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        gameToEdit.setName(gameRequestDTO.getNameChange());

        if(!gameRequestDTO.getName().equals(gameRequestDTO.getNameChange()))
        gameResourcesService.renameGamesRules(gameRequestDTO.getName(),gameRequestDTO.getNameChange());

        return this.gameRepository.save(gameToEdit);
    }

    public Game getGame(String gameUniqueName) {
        return Optional.ofNullable(gameRepository.findNotBannedGameByUniqueName(gameUniqueName))
                .orElseThrow(() -> new ObjectNotFoundException(Game.class,gameUniqueName));
    }
}
