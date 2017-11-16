package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import pl.edu.pollub.battleCraft.additionalClasses.Factorial;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;

import java.util.*;
import java.util.stream.Collectors;

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
        this.validateTournamentName(tournamentWebDTO.getName());
        this.validateTournamentName(tournamentWebDTO.getNameChange());
        this.validatePlayersOnTableCount(tournamentWebDTO.getPlayersOnTableCount());
        this.validateTablesCount(tournamentWebDTO.getPlayersOnTableCount(),tournamentWebDTO.getTablesCount());
        this.validateToursCount(tournamentWebDTO.getToursCount(),
                tournamentWebDTO.getTablesCount());
        this.validateStartDate(tournamentWebDTO.getDateOfStart());
        this.validateEndDate(tournamentWebDTO.getDateOfStart(),tournamentWebDTO.getDateOfEnd());
        addressValidator.validate(tournamentWebDTO,errors);
    }

    private void validateTournamentName(String tournamentName){
        if(tournamentName==null || !tournamentName.matches("^[A-ZĄĆĘŁŃÓŚŹŻ][A-Za-zzżźćńółęąśŻŹĆĄŚĘŁÓŃ0-9 ]{1,19}$"))
            errors.rejectValue("nameChange","","Tournament name must start with big letter and have between 2 to 30 chars");
    }

    private void validatePlayersOnTableCount(int playersOnTableCount){
        if(playersOnTableCount!=2 && playersOnTableCount!=4)
            errors.rejectValue("playersOnTable","",
                    new StringBuilder("You can choose only 2 or 4 Player count on table").toString());
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

    private void validateToursCount(int toursCount, int tablesCount){
        int maxPlayers = (2 * tablesCount);
        int maxToursNumber = maxPlayers - 1;
        if(toursCount>maxToursNumber)
            errors.rejectValue("toursCount","",
                    new StringBuilder("Max tours number in this tournament is: ").append(maxToursNumber).toString());
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
        Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.getNameChange());
        if(tournamentExist!=null)
            bindingResult.rejectValue("nameChange","","Tournament with this name already exist.");
    }

    public void checkIfTournamentWithThisNameAlreadyExist(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult){
        if(!tournamentWebDTO.getName().equals(tournamentWebDTO.getNameChange())) {
            Tournament tournamentExist = tournamentRepository.findTournamentToEditByUniqueName(tournamentWebDTO.getNameChange());
            if (tournamentExist != null)
                bindingResult.rejectValue("nameChange", "", "Tournament with this name already exist.");
        }
    }

    public Tournament getValidatedTournamentToEdit(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        Tournament tournamentToEdit = Optional.ofNullable(tournamentRepository.findByName(tournamentWebDTO.getName()))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentWebDTO.getName()));//TO DO: if player is administrator
        if(tournamentToEdit.isBanned() || (tournamentToEdit.getStatus()!= TournamentStatus.ACCEPTED && tournamentToEdit.getStatus()!=TournamentStatus.NEW)){
            bindingResult.rejectValue("nameChange","","This tournament is not accepted or new");
        }
        return tournamentToEdit;
    }

    public Game getValidatedGame(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        Game tournamentGame = gameRepository.findAcceptedGameByUniqueName(tournamentWebDTO.getGame());
        if(tournamentGame==null){
            bindingResult.rejectValue("game","", new StringBuilder("game: ").append(tournamentWebDTO.getGame()).append(" does not exist").toString());
        }
        return tournamentGame;
    }

    public List<List<Player>> getValidatedParticipants(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        if(tournamentWebDTO.getParticipants().size()==0)
            return new ArrayList<>();
        List<String> flatMapOfPlayersNames = tournamentWebDTO.getParticipants().stream()
                .flatMap(Collection::stream).collect(Collectors.toList());
        if(containsDuplicates(flatMapOfPlayersNames))
            bindingResult.rejectValue("participants","","You can invite player only once");
        List<Player> participantsList = playerRepository.findPlayersByUniqueName(flatMapOfPlayersNames);
        List<List<Player>> groupedParticipants;
        if(tournamentWebDTO.getTournamentType() == TournamentType.GROUP){
            groupedParticipants = this.groupPlayers(tournamentWebDTO.getParticipants(),participantsList);
            this.checkIfPlayersCountIsGreaterThanLimit(bindingResult,tournamentWebDTO,groupedParticipants);
        }
        else{
            groupedParticipants = this.groupPlayers(participantsList);
            this.checkIfPlayersCountIsGreaterThanLimit(bindingResult,tournamentWebDTO,groupedParticipants);
        }
        return groupedParticipants;
    }

    public List<Organizer> getValidatedOrganizers(TournamentRequestDTO tournamentWebDTO,BindingResult bindingResult){
        if(tournamentWebDTO.getOrganizers().length==0)
            return new ArrayList<>();
        if(containsDuplicates(tournamentWebDTO.getOrganizers()))
            bindingResult.rejectValue("Organizer","","You can invite organizer only once");
        List<Organizer> organizersList = organizerRepository.findOrganizersByUniqueNames(tournamentWebDTO.getOrganizers());
        if(organizersList.size()>10)
            bindingResult.rejectValue("Organizer","","Count of Organizer must be less than 10");
        return organizersList;
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid tournament data", bindingResult);
        }
    }

    private List<List<Player>> groupPlayers(List<List<String>> groupsPatterns, List<Player> players){
        List<List<Player>> groupedPlayers = new ArrayList<>();
        List<String> playersNames = players.stream().map(UserAccount::getName).collect(Collectors.toList());

        groupsPatterns.forEach( groupPattern -> {
            String playerName1 = groupPattern.get(0);
            if(groupPattern.size() == 1){
                if(playersNames.contains(playerName1))
                groupedPlayers.add(Collections.singletonList(this.getPlayerByName(players,playerName1)));
            }
            if(groupPattern.size() == 2) {
                String playerName2 = groupPattern.get(1);
                if(!playersNames.contains(playerName1)){
                    groupedPlayers.add(Collections.singletonList(this.getPlayerByName(players,playerName2)));
                }
                else if(!playersNames.contains(playerName2)){
                    groupedPlayers.add(Collections.singletonList(this.getPlayerByName(players,playerName1)));
                }
                else {
                    groupedPlayers.add(Arrays.asList(
                            this.getPlayerByName(players, playerName1),
                            this.getPlayerByName(players, playerName2)
                    ));
                }
            }
        });

        return groupedPlayers;
    }

    private List<List<Player>> groupPlayers(List<Player> players){
        List<List<Player>> groupedPlayers = new ArrayList<>();
        players.forEach(player -> {
            groupedPlayers.add(Collections.singletonList(player));
        });
        return groupedPlayers;
    }

    private Player getPlayerByName(List<Player> players, String name){
        return players.stream().filter(player -> player.getName().equals(name)).findFirst().get();
    }

    private boolean containsDuplicates(String[] values){
        Set<String> setWithoutDuplicates = new HashSet<>(Arrays.asList(values));
        if(setWithoutDuplicates.size()<values.length)
            return true;
        return false;
    }

    private boolean containsDuplicates(List<String> values){
        Set<String> setWithoutDuplicates = new HashSet<>(values);
        if(setWithoutDuplicates.size()<values.size())
            return true;
        return false;
    }

    private void checkIfPlayersCountIsGreaterThanLimit(BindingResult bindingResult, TournamentRequestDTO tournamentWebDTO, List<List<Player>> groupedParticipants){
        if(groupedParticipants.size() * tournamentWebDTO.getPlayersOnTableCount()/2>tournamentWebDTO.getPlayersOnTableCount()*tournamentWebDTO.getTablesCount())
            bindingResult.rejectValue("participants","",
                    new StringBuilder("Participants count must be less than ").append(tournamentWebDTO.getPlayersOnTableCount()*tournamentWebDTO.getTablesCount()).toString());
    }
}
