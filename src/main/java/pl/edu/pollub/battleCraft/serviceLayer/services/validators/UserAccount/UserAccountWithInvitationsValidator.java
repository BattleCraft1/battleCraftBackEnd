package pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccount;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.DuelTournamentParticipationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.GroupTournamentParticipationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.ParticipationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation.OrganizationRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation.ParticipationRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.WithPariticipation.UserAccountWithParticipationRequestDTO;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserAccountWithInvitationsValidator implements Validator {

    private Errors errors;

    private final TournamentRepository tournamentRepository;

    private final PlayerRepository playerRepository;

    private final UserAccountRepository userAccountRepository;

    private final UserAccountValidator userAccountValidator;

    private final AuthorityRecognizer authorityRecognizer;

    public UserAccountWithInvitationsValidator(TournamentRepository tournamentRepository, PlayerRepository playerRepository, UserAccountRepository userAccountRepository, UserAccountValidator userAccountValidator, AuthorityRecognizer authorityRecognizer) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.userAccountRepository = userAccountRepository;
        this.userAccountValidator = userAccountValidator;
        this.authorityRecognizer = authorityRecognizer;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserAccountWithParticipationRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        this.errors = errors;
        UserAccountWithParticipationRequestDTO userAccountRequestDTO = (UserAccountWithParticipationRequestDTO) o;
        userAccountValidator.validate(userAccountRequestDTO,errors);
    }

    //

    public void checkIfUserWithThisNameOrEmailAlreadyExist(UserAccountWithParticipationRequestDTO userAccountRequestDTO){
        if(!userAccountRequestDTO.getName().equals(userAccountRequestDTO.getNameChange())) {

            authorityRecognizer.checkIfCurrentUserCanModifyUsername(userAccountRequestDTO.getName());
            UserAccount userExist = userAccountRepository.findUserAccountByUniqueNameOrEmail(userAccountRequestDTO.getNameChange(),userAccountRequestDTO.getEmail());

            if (userExist.getName().equals(userAccountRequestDTO.getNameChange()))
                errors.rejectValue("nameChange", "", "User with this name already exist.");
            if (!userExist.getName().equals(userAccountRequestDTO.getName()) && userExist.getEmail().equals(userAccountRequestDTO.getEmail()))
                errors.rejectValue("email", "", "User with this email already exist.");
        }
        else{
            UserAccount userExist = userAccountRepository.findUserAccountByEmailWithOtherUniqueName(userAccountRequestDTO.getNameChange(),userAccountRequestDTO.getEmail());

            if (userExist!=null)
                errors.rejectValue("email", "", "User with this email already exist.");
        }
    }

    //TO DO: Eliminate n+1 problem with criteria api
    public List<ParticipationDTO> getValidatedParticipation(UserAccountWithParticipationRequestDTO userAccountRequestDTO){
        List<ParticipationRequestDTO> allPlayerParticipation = userAccountRequestDTO.getParticipatedTournaments();

        if(allPlayerParticipation.size()==0)
            return new ArrayList<>();

        List<String> tournamentsNames = allPlayerParticipation.stream()
                .map(ParticipationRequestDTO::getTournamentName)
                .collect(Collectors.toList());

        if(containsDuplicates(tournamentsNames))
            errors.rejectValue("participatedTournaments","","You cannot have duplicated tournament");

        List<Tournament> tournaments = tournamentRepository.findAcceptedOrNewTournamentsByUniqueNames(tournamentsNames);

        List<String> tournamentsNamesWithTooManyPlayers = new ArrayList<>();

        List<ParticipationDTO> output = tournaments.stream().map(
                tournament -> checkParticipationInTournamentIsValid(
                        userAccountRequestDTO.getName(),
                        tournamentsNamesWithTooManyPlayers,
                        tournament,
                        allPlayerParticipation
                )
        ).collect(Collectors.toList());

        if(tournamentsNamesWithTooManyPlayers.size()>0)
            this.reportTournamentsWithTooManyPlayers(tournamentsNamesWithTooManyPlayers);

        return output;
    }

    //TO DO: Eliminate n+1 problem with criteria api
    public List<ParticipationDTO> getValidatedOrganization(List<OrganizationRequestDTO> organizations){
        if(organizations.size()==0)
            return new ArrayList<>();

        List<String> tournamentsNames = organizations.stream()
                .map(OrganizationRequestDTO::getTournamentName)
                .collect(Collectors.toList());

        if(containsDuplicates(tournamentsNames))
            errors.rejectValue("organizedTournaments","","You cannot have duplicated tournament");

        List<Tournament> tournaments = tournamentRepository.findAcceptedOrNewTournamentsByUniqueNames(tournamentsNames);

        List<String> tournamentsNamesWithTooManyOrganizers = new ArrayList<>();

        List<ParticipationDTO> output = tournaments.stream().map(
                tournament -> checkOrganizationInTournamentIsValid(
                        tournamentsNamesWithTooManyOrganizers,
                        tournament,
                        organizations
                )
        ).collect(Collectors.toList());

        if(tournamentsNamesWithTooManyOrganizers.size()>0)
            this.reportTournamentsWithTooManyOrganizers(tournamentsNamesWithTooManyOrganizers);

        return output;
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid user account data", bindingResult);
        }
    }

    private ParticipationDTO checkOrganizationInTournamentIsValid(List<String> tournamentsNamesWithTooManyOrganizers,
                                                                    Tournament tournament,
                                                                    List<OrganizationRequestDTO> organizations){
        boolean accepted = this.checkIfParticipationIsAcceptedForTournament(organizations,tournament.getName());
        if(tournament.getOrganizations().size()>=10)
            tournamentsNamesWithTooManyOrganizers.add(tournament.getName());
        return new ParticipationDTO(tournament,accepted);
    }

    private ParticipationDTO checkParticipationInTournamentIsValid(String username,
                                                                   List<String> tournamentsNamesWithTooManyPlayers,
                                                                   Tournament tournament,
                                                                   List<ParticipationRequestDTO> allPlayerParticipation){
        if(tournament.getTournamentType() == TournamentType.GROUP){

            ParticipationRequestDTO participation = this.getParticipationByTournamentName(allPlayerParticipation,tournament.getName());
            Player player;

            if(participation.getSecondPlayerName().equals(username)){
                player = new NullPlayer();
            }
            else{
                player = Optional.ofNullable(playerRepository.findNotBannedPlayerByUniqueName(participation.getSecondPlayerName()))
                        .orElse(new NullPlayer());
            }

            this.checkIfPlayerAlreadyHaveParticipationWithSecondPlayer(tournament,player,username);
            this.checkIfPlayersCountIsGreaterThanLimit(tournamentsNamesWithTooManyPlayers,tournament,username);

            return new GroupTournamentParticipationDTO(tournament, participation.isAccepted(),player);
        }
        else{
            this.checkIfPlayersCountIsGreaterThanLimit(tournamentsNamesWithTooManyPlayers,tournament,username);
            ParticipationRequestDTO invitation = this.getParticipationByTournamentName(allPlayerParticipation,tournament.getName());

            return new DuelTournamentParticipationDTO(tournament,invitation.isAccepted());
        }
    }

    private ParticipationRequestDTO getParticipationByTournamentName(List<ParticipationRequestDTO> allParticipation, String tournamentName){
        return allParticipation.stream()
                .filter(participation -> participation.getTournamentName().equals(tournamentName))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentName));
    }

    private boolean checkIfParticipationIsAcceptedForTournament(List<OrganizationRequestDTO> invitations, String tournamentName){
        return invitations.stream().filter(participationRequestDTO -> participationRequestDTO.getTournamentName().equals(tournamentName))
                .findFirst().get().isAccepted();
    }

    private boolean containsDuplicates(List<String> values){
        Set<String> setWithoutDuplicates = new HashSet<>(values);
        return setWithoutDuplicates.size() < values.size();
    }

    private void checkIfPlayersCountIsGreaterThanLimit(List<String> tournamentsNamesWithTooManyPlayers, Tournament tournament, String playerName) {
            int requiredSlotsCount = tournament.getPlayersOnTableCount()/2;
            Participation participationOfPlayer = tournament.getParticipation().stream()
                .filter(participation -> participation.getPlayer().getName().equals(playerName))
                .findFirst().orElse(null);

            if(participationOfPlayer==null && tournament.getParticipation().size()+requiredSlotsCount>=tournament.getMaxPlayers()){
                tournamentsNamesWithTooManyPlayers.add(tournament.getName());
            }
    }

    private void checkIfPlayerAlreadyHaveParticipationWithSecondPlayer(Tournament tournament, Player secondPlayer, String userName){
        Participation participationOfSecondPlayer = tournament.getParticipation().stream()
                .filter(participation -> participation.getPlayer().equals(secondPlayer)).findFirst().orElse(null);

        if(participationOfSecondPlayer!=null){
            Participation participationOfEditedPlayer = tournament.getParticipation().stream()
                    .filter(participation -> participation.getPlayer().getName().equals(userName)).findFirst().orElse(null);

            if(participationOfEditedPlayer==null || !ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(participationOfEditedPlayer,participationOfSecondPlayer)){
                errors.rejectValue("participatedTournaments","", new StringBuilder("Player: ").append(secondPlayer.getName()).append(" already participated in tournament: ").append(tournament.getName()).toString());
            }
        }
    }

    private void reportTournamentsWithTooManyPlayers(List<String> tournamentsNamesWithTooManyPlayers){
        StringBuilder raport = new StringBuilder("Tournaments: ")
                .append(tournamentsNamesWithTooManyPlayers.stream().collect(Collectors.joining(", ")))
                .append(" have too many players");
        errors.rejectValue("participatedTournaments","",raport.toString());
    }

    private void reportTournamentsWithTooManyOrganizers(List<String> tournamentsNamesWithTooManyPlayers){
        StringBuilder raport = new StringBuilder("Tournaments: ")
                .append(tournamentsNamesWithTooManyPlayers.stream().collect(Collectors.joining(", ")))
                .append(" have too many organizers");
        errors.rejectValue("organizedTournaments","",raport.toString());
    }
}
