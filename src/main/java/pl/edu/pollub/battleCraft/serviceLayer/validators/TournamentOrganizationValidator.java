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
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import java.util.Calendar;
import java.util.Date;

@Component
public class TournamentOrganizationValidator implements Validator {
    private Errors errors;

    private final AddressValidator addressValidator;

    public TournamentOrganizationValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TournamentResponseDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        TournamentRequestDTO tournamentWebDTO = (TournamentRequestDTO) o;
        this.validateTournamentName(tournamentWebDTO.name);
        this.validateTournamentName(tournamentWebDTO.nameChange);
        this.validateTablesCount(tournamentWebDTO.tablesCount);
        this.validateMaxPlayers(tournamentWebDTO.maxPlayers,tournamentWebDTO.tablesCount);
        this.validateStartDate(tournamentWebDTO.dateOfStart);
        this.validateEndDate(tournamentWebDTO.dateOfStart,tournamentWebDTO.dateOfEnd);
        addressValidator.validate(tournamentWebDTO,errors);
    }

    private void validateTournamentName(String tournamentName){
        if(tournamentName==null || !tournamentName.matches("^[A-Z][A-Za-zzżźćńółęąśŻŹĆĄŚĘŁÓŃ0-9 ]{1,29}$"))
            errors.rejectValue("name","","Tournament name must start with big letter and have between 2 to 30 chars");
    }

    private void validateTablesCount(int tablesCount){
        if(tablesCount<1 || tablesCount>30)
            errors.rejectValue("tablesCount","","Tables count must be between 1 and 30");
    }

    private void validateMaxPlayers(int maxPlayers, int tablesCount){
        if(maxPlayers>tablesCount*2)
            errors.rejectValue("maxPlayersCount","",
                    new StringBuilder("You cannot create tournament with ")
                    .append(maxPlayers)
                    .append(" players count because if you have ")
                    .append(tablesCount)
                    .append(" you can have only ")
                    .append(tablesCount*2)
                    .append(" players").toString());
    }

    private void validateStartDate(Date startDate){
        if(startDate==null || startDate.before(new Date()))
            errors.rejectValue("dateOfStart","", new StringBuilder("you cannot start tournament at: ")
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

    public void validateDataFromDatabase(Game tournamentGame, Player[] participants,
                                          Organizer[] organizers, BindingResult bindingResult,
                                          TournamentRequestDTO tournamentWebDTO) throws EntityValidationException {
        if(tournamentGame==null){
            bindingResult.rejectValue("game","", new StringBuilder("game: ").append(tournamentWebDTO.game).append(" does not exist").toString());
        }

        if(participants.length<1 || participants.length>tournamentWebDTO.tablesCount*2)
            bindingResult.rejectValue("participants","",
                    new StringBuilder("Participants count must be between 1 and ").append(tournamentWebDTO.tablesCount*2).toString());

        if(organizers.length<1 || organizers.length>10)
            bindingResult.rejectValue("organizers","","count of organizers must be between 1 and 15");

        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid tournament data", bindingResult);
        }
    }

    public void validateDataToEditFromDatabase(Tournament tournament,Game tournamentGame, Player[] participants, Organizer[] organizers, BindingResult bindingResult, TournamentRequestDTO tournamentWebDTO) {
        if(tournament.isBanned() || (tournament.getStatus()!= TournamentStatus.ACCEPTED && tournament.getStatus()!=TournamentStatus.NEW)){
            bindingResult.rejectValue("name","","This tournament is not accepted");
        }

        this.validateDataFromDatabase(tournamentGame,participants,organizers,bindingResult,tournamentWebDTO);
    }
}
