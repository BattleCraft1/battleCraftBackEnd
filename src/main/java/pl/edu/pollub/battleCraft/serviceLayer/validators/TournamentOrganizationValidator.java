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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TournamentOrganizationValidator implements Validator {
    private Errors errors;

    private final TournamentRepository tournamentRepository;

    private final AddressValidator addressValidator;

    private final OrganizerRepository organizerRepository;

    private final PlayerRepository playerRepository;

    private final GameRepository gameRepository;

    public TournamentOrganizationValidator(TournamentRepository tournamentRepository, AddressValidator addressValidator, OrganizerRepository organizerRepository, PlayerRepository playerRepository, GameRepository gameRepository) {
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
        this.validateTablesCount(tournamentWebDTO.tablesCount);
        this.validateMaxPlayers(tournamentWebDTO.maxPlayers,tournamentWebDTO.tablesCount);
        this.validateStartDate(tournamentWebDTO.dateOfStart);
        this.validateEndDate(tournamentWebDTO.dateOfStart,tournamentWebDTO.dateOfEnd);
        addressValidator.validate(tournamentWebDTO,errors);
    }

    private void validateTournamentName(String tournamentName){
        if(tournamentName==null || !tournamentName.matches("^[A-Z][A-Za-zzżźćńółęąśŻŹĆĄŚĘŁÓŃ0-9 ]{1,29}$"))
            errors.rejectValue("nameChange","","Tournament name must start with big letter and have between 2 to 30 chars");
    }

    private void validateTablesCount(int tablesCount){
        if(tablesCount<1 || tablesCount>30)
            errors.rejectValue("tablesCount","","Tables count must be between 1 and 30");
    }

    private void validateMaxPlayers(int maxPlayers, int tablesCount){
        if(maxPlayers>tablesCount*2)
            errors.rejectValue("maxPlayers","",
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
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(tournamentWebDTO.participants);
        Player[] participants = participantsList.toArray(new Player[participantsList.size()]);
        if(participants.length<1 || participants.length>tournamentWebDTO.tablesCount*2)
            bindingResult.rejectValue("participants","",
                    new StringBuilder("Participants count must be between 1 and ").append(tournamentWebDTO.tablesCount*2).toString());
        return participants;
    }

    public Organizer[] getValidatedOrganizers(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.organizers);
        Organizer[] organizers = organizersList.toArray(new Organizer[organizersList.size()]);
        if(organizers.length<1 || organizers.length>10)
            bindingResult.rejectValue("organizers","","count of organizers must be between 1 and 15");
        return organizers;
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid tournament data", bindingResult);
        }
    }
}
