package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;

import java.util.Optional;

@Component
public class GameValidator implements Validator {

    private final GameRepository gameRepository;

    @Autowired
    public GameValidator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return GameRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        GameRequestDTO gameRequestDTO = (GameRequestDTO) o;
        if(gameRequestDTO.getName()==null
                || gameRequestDTO.getNameChange().length()<1
                || gameRequestDTO.getNameChange().length()>50)
            errors.rejectValue("nameChange","","Game name must have between 1 to 50 chars");
    }

    public void checkIfGameExist(GameRequestDTO gameRequestDTO,BindingResult bindingResult){
        Game gameExist = gameRepository.findGameByUniqueName(gameRequestDTO.getNameChange());
        if(gameExist!=null)
            bindingResult.rejectValue("nameChange","","Game with this name already exist.");
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid game data", bindingResult);
        }
    }

    public void checkIfGameToEditExist(GameRequestDTO gameRequestDTO,BindingResult bindingResult){
        if(!gameRequestDTO.getName().equals(gameRequestDTO.getNameChange())) {
            Game gameExist = gameRepository.findGameByUniqueName(gameRequestDTO.getNameChange());
            if (gameExist != null)
                bindingResult.rejectValue("nameChange", "", "Game with this name already exist.");
        }
    }

    //TO DO: Eliminate n+1 problem with criteria api
    public Game getValidatedGameToEdit(GameRequestDTO gameRequestDTO,BindingResult bindingResult){
        Game gameToEdit = Optional.ofNullable(gameRepository.findGameByUniqueName(gameRequestDTO.getName()))
                .orElseThrow(() -> new ObjectNotFoundException(Game.class,gameRequestDTO.getName()));
        if(gameToEdit.isBanned() || (gameToEdit.getStatus()!= GameStatus.ACCEPTED && gameToEdit.getStatus()!=GameStatus.NEW)){
            bindingResult.rejectValue("nameChange","","This game is not accepted or new");
        }
        return gameToEdit;
    }
}
