package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.GameResourcesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.GameValidator;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;

import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final GameValidator gameValidator;

    private final UserAccountRepository userAccountRepository;

    private final GameResourcesService gameResourcesService;

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public GameService(GameRepository gameRepository, GameValidator gameValidator, UserAccountRepository userAccountRepository, GameResourcesService gameResourcesService, AuthorityRecognizer authorityRecognizer) {
        this.gameRepository = gameRepository;
        this.gameValidator = gameValidator;
        this.userAccountRepository = userAccountRepository;
        this.gameResourcesService = gameResourcesService;
        this.authorityRecognizer = authorityRecognizer;
    }

    @Transactional
    public Game addGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {
        String organizerName = authorityRecognizer.getCurrentUserNameFromContext();
        UserAccount creator = Optional.ofNullable(userAccountRepository.findByName(organizerName))
                .orElseThrow(() -> new ObjectNotFoundException(Organizer.class,organizerName));

        gameValidator.checkIfGameExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        Game createdGame = new Game(gameRequestDTO.getName(),creator);

        return this.gameRepository.save(createdGame);
    }

    @Transactional(rollbackFor = {EntityValidationException.class,ObjectNotFoundException.class})
    public Game editGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult) {

        Game gameToEdit = Optional.ofNullable(gameRepository.findGameByUniqueName(gameRequestDTO.getName()))
                .orElseThrow(() -> new ObjectNotFoundException(Game.class,gameRequestDTO.getName()));

        authorityRecognizer.checkIfCurrentUserIsCreatorOfGame(gameToEdit);

        gameValidator.checkIfGameToEditExist(gameRequestDTO,bindingResult);
        gameValidator.validate(gameRequestDTO,bindingResult);

        gameValidator.finishValidation(bindingResult);

        gameToEdit.setName(gameRequestDTO.getNameChange());

        if(!gameRequestDTO.getName().equals(gameRequestDTO.getNameChange()))
        gameResourcesService.renameGamesRules(gameRequestDTO.getName(),gameRequestDTO.getNameChange());

        return this.gameRepository.save(gameToEdit);
    }

    public Game getGame(String gameUniqueName) {
        Game game = Optional.ofNullable(gameRepository.findGameByUniqueName(gameUniqueName))
                .orElseThrow(() -> new ObjectNotFoundException(Game.class,gameUniqueName));
        authorityRecognizer.checkIfUserCanFetchGame(game);
        return game;
    }
}
