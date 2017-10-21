package pl.edu.pollub.battleCraft.serviceLayer.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;

import java.util.*;

@Component
public class TournamentValidator implements Validator {
    private Errors errors;

    private final TournamentRepository tournamentRepository;

    private final AddressValidator addressValidator;

    private final OrganizerRepository organizerRepository;

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    public TournamentValidator(TournamentRepository tournamentRepository, AddressValidator addressValidator, OrganizerRepository organizerRepository, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.tournamentRepository = tournamentRepository;
        this.addressValidator = addressValidator;
        this.organizerRepository = organizerRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TournamentRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        TournamentRequestDTO tournamentWebDTO = (TournamentRequestDTO) o;
        this.validateTournamentName(tournamentWebDTO.name);
        this.validateTournamentName(tournamentWebDTO.nameChange);
        this.validatePlayersOnTableCount(tournamentWebDTO.playersOnTableCount);
        this.validateTablesCount(tournamentWebDTO.playersOnTableCount,tournamentWebDTO.tablesCount);
        this.validateStartDate(tournamentWebDTO.dateOfStart);
        this.validateEndDate(tournamentWebDTO.dateOfStart,tournamentWebDTO.dateOfEnd);
        addressValidator.validate(tournamentWebDTO,errors);
    }

    private void validateTournamentName(String tournamentName){
        if(tournamentName==null || !tournamentName.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][A-Za-zzżźćńółęąśŻŹĆĄŚĘŁÓŃ0-9 ]{1,19}$"))
            errors.rejectValue("nameChange","","Tournament name must start with big letter and have between 2 to 30 chars");
    }

    private void validateTablesCount(int playersOnTableCount, int tablesCount){
        if(playersOnTableCount==2){
            if(tablesCount<1 || tablesCount>30)
                errors.rejectValue("tablesCount","","Tables count must be between 1 and 30");
        }
        else if(playersOnTableCount==4){
            if(tablesCount<1 || tablesCount>15)
                errors.rejectValue("tablesCount","","Tables count must be between 1 and 15");
        }
    }

    private void validatePlayersOnTableCount(int playersOnTableCount){
        if(playersOnTableCount!=2 && playersOnTableCount!=4)
            errors.rejectValue("playersOnTable","",
                    new StringBuilder("You can choose only 2 or 4 players count on table").toString());
    }

    private void validateStartDate(Date startDate){
        if(startDate==null || startDate.before(new Date()))
            errors.rejectValue("dateOfStart","", new StringBuilder("You cannot start tournament at: ")
                    .append(startDate).append(" because this date is outdated").toString());
    }

    private void validateEndDate(Date startDate, Date endDate){
        if(endDate==null || endDate.before(startDate)){
            errors.rejectValue("dateOfEnd","", new StringBuilder("End date must be later than ").append(startDate).toString());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE,3);
        Date maxEndDate = calendar.getTime();
        if(endDate.after(maxEndDate)){
            errors.rejectValue("dateOfEnd","","Duration of tournament cannnot be longer than 3 days");
        }
    }

    public void checkIfTournamentExist(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.nameChange);
        if(tournamentExist!=null)
            bindingResult.rejectValue("nameChange","","Tournament with this name already exist.");
    }

    public void checkIfTournamentToEditExist(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        if(!tournamentWebDTO.name.equals(tournamentWebDTO.nameChange)) {
            Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.nameChange);
            if (tournamentExist != null)
                bindingResult.rejectValue("nameChange", "", "Tournament with this name already exist.");
        }
    }

    public Tournament getValidatedTournamentToEdit(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        Tournament tournamentToEdit = Optional.ofNullable(tournamentRepository.findByName(tournamentWebDTO.name))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentWebDTO.name));
        if(tournamentToEdit.isBanned() || (tournamentToEdit.getStatus()!= TournamentStatus.ACCEPTED && tournamentToEdit.getStatus()!=TournamentStatus.NEW)){
            bindingResult.rejectValue("nameChange","","This tournament is not accepted");
        }
        return tournamentToEdit;
    }

    public Game getValidatedGame(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        Game tournamentGame = gameRepository.findAcceptedGameByName(tournamentWebDTO.game);
        if(tournamentGame==null){
            bindingResult.rejectValue("game","", new StringBuilder("game: ").append(tournamentWebDTO.game).append(" does not exist").toString());
        }
        return tournamentGame;
    }

    public Player[] getValidatedParticipants(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        if(tournamentWebDTO.participants.length==0)
            return new Player[] {};
        if(containsDuplicates(tournamentWebDTO.participants))
            bindingResult.rejectValue("participants","","you can invite player only once");
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(tournamentWebDTO.participants);
        Player[] participants = participantsList.toArray(new Player[participantsList.size()]);
        if(participants.length>tournamentWebDTO.playersOnTableCount*tournamentWebDTO.tablesCount)
            bindingResult.rejectValue("participants","",
                    new StringBuilder("Participants count must be less than ").append(tournamentWebDTO.playersOnTableCount*tournamentWebDTO.tablesCount).toString());
        return participants;
    }

    public Organizer[] getValidatedOrganizers(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        if(tournamentWebDTO.organizers.length==0)
            return new Organizer[] {};
        if(containsDuplicates(tournamentWebDTO.organizers))
            bindingResult.rejectValue("organizers","","you can invite organizer only once");
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.organizers);
        Organizer[] organizers = organizersList.toArray(new Organizer[organizersList.size()]);
        if(organizers.length>10)
            bindingResult.rejectValue("organizers","","Count of organizers must be less than 10");
        return organizers;
    }

    private boolean containsDuplicates(String[] values){
        Set<String> setWithoutDuplicates = new HashSet<>(Arrays.asList(values));
        if(setWithoutDuplicates.size()<values.length)
            return true;
        return false;
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid tournament data", bindingResult);
        }
    }
}
