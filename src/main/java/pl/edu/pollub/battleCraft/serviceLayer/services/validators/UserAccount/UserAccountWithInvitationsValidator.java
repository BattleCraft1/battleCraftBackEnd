package pl.edu.pollub.battleCraft.serviceLayer.services.validators.UserAccount;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.DuelTournamentInvitationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.GroupTournamentInvitationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.InvitationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestPlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.UserAccountWithInvitationsRequestDTO;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserAccountWithInvitationsValidator implements Validator {

    private final TournamentRepository tournamentRepository;

    private final PlayerRepository playerRepository;

    private final UserAccountRepository userAccountRepository;

    private final UserAccountValidator userAccountValidator;

    public UserAccountWithInvitationsValidator(TournamentRepository tournamentRepository, PlayerRepository playerRepository, UserAccountRepository userAccountRepository, UserAccountValidator userAccountValidator) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.userAccountRepository = userAccountRepository;
        this.userAccountValidator = userAccountValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserAccountWithInvitationsRequestDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserAccountWithInvitationsRequestDTO userAccountRequestDTO = (UserAccountWithInvitationsRequestDTO) o;
        userAccountValidator.validate(userAccountRequestDTO,errors);
    }

    //

    public void checkIfUserWithThisNameOrEmailAlreadyExist(UserAccountWithInvitationsRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        if(!userAccountRequestDTO.getName().equals(userAccountRequestDTO.getNameChange())) {
            UserAccount userExist = userAccountRepository.findUserAccountByUniqueNameOrEmail(userAccountRequestDTO.getNameChange(),userAccountRequestDTO.getEmail());
            if (userExist.getName().equals(userAccountRequestDTO.getNameChange()))
                bindingResult.rejectValue("nameChange", "", "User with this name already exist.");
            if (!userExist.getName().equals(userAccountRequestDTO.getName()) && userExist.getEmail().equals(userAccountRequestDTO.getEmail()))
                bindingResult.rejectValue("email", "", "User with this email already exist.");
        }
        else{
            UserAccount userExist = userAccountRepository.findUserAccountByEmailWithOtherUniqueName(userAccountRequestDTO.getNameChange(),userAccountRequestDTO.getEmail());
            if (userExist!=null)
                bindingResult.rejectValue("email", "", "User with this email already exist.");
        }
    }

    //TO DO: Eliminate n+1 problem with criteria api
    public List<InvitationDTO> getValidatedPlayersInvitations(String username, UserAccountWithInvitationsRequestDTO userAccountRequestDTO, BindingResult bindingResult){
        List<InvitationRequestPlayerDTO> invitations = userAccountRequestDTO.getParticipatedTournaments();
        if(invitations.size()==0)
            return new ArrayList<>();
        List<String> tournamentsNames = invitations.stream()
                .map(InvitationRequestPlayerDTO::getTournamentName)
                .collect(Collectors.toList());
        if(containsDuplicates(tournamentsNames))
            bindingResult.rejectValue("participatedTournaments","","You cannot have duplicated tournament");

        List<Tournament> tournaments = tournamentRepository.findAcceptedOrNewTournamentsByUniqueNames(tournamentsNames);
        List<String> tournamentsNamesWithTooManyPlayers = new ArrayList<>();
        List<InvitationDTO> output = tournaments.stream().map(tournament -> {
            if(tournament.getTournamentType()== TournamentType.GROUP){
                InvitationRequestPlayerDTO invitation = this.getInvitationByTournamentName(invitations,tournament.getName());
                Player player;
                if(invitation.getSecondPlayerName().equals(username))
                    player = null;
                else
                    player = playerRepository.findNotBannedPlayerByUniqueName(invitation.getSecondPlayerName());
                this.checkIfPlayerAlreadyHaveParticipation(tournament,player,username,bindingResult);
                this.checkIfPlayersCountIsGreaterThanLimit(tournamentsNamesWithTooManyPlayers,tournament,username,2);
                return new GroupTournamentInvitationDTO(tournament, invitation.isAccepted(),player);
            }
            else{
                this.checkIfPlayersCountIsGreaterThanLimit(tournamentsNamesWithTooManyPlayers,tournament,username,1);
                InvitationRequestPlayerDTO invitation = this.getInvitationByTournamentName(invitations,tournament.getName());
                return new DuelTournamentInvitationDTO(tournament,invitation.isAccepted());
            }
        }).collect(Collectors.toList());
        if(tournamentsNamesWithTooManyPlayers.size()>0)
            this.reportTournamentsWithTooManyPlayers(tournamentsNamesWithTooManyPlayers,bindingResult);
        return output;
    }

    //TO DO: Eliminate n+1 problem with criteria api
    public List<InvitationDTO> getValidatedOrganizersInvitations(List<InvitationRequestDTO> invitations, BindingResult bindingResult){
        if(invitations.size()==0)
            return new ArrayList<>();
        List<String> tournamentsNames = invitations.stream()
                .map(InvitationRequestDTO::getTournamentName)
                .collect(Collectors.toList());
        if(containsDuplicates(tournamentsNames))
            bindingResult.rejectValue("organizedTournaments","","You cannot have duplicated tournament");
        List<Tournament> tournaments = tournamentRepository.findAcceptedOrNewTournamentsByUniqueNames(tournamentsNames);

        List<String> tournamentsNamesWithTooManyOrganizers = new ArrayList<>();
        List<InvitationDTO> output = tournaments.stream().map(tournament -> {
                boolean invitationAccepted = this.checkIfInvitationIsAcceptedForTournament(invitations,tournament.getName());
                if(tournament.getOrganizations().size()>=10)
                    tournamentsNamesWithTooManyOrganizers.add(tournament.getName());
                return new InvitationDTO(tournament,invitationAccepted);
        }).collect(Collectors.toList());
        if(tournamentsNamesWithTooManyOrganizers.size()>0)
            this.reportTournamentsWithTooManyOrganizers(tournamentsNamesWithTooManyOrganizers,bindingResult);
        return output;
    }

    public void finishValidation(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new EntityValidationException("Invalid user account data", bindingResult);
        }
    }

    private InvitationRequestPlayerDTO getInvitationByTournamentName(List<InvitationRequestPlayerDTO> invitations, String tournamentName){
        return invitations.stream()
                .filter(invitation -> invitation.getTournamentName().equals(tournamentName))
                .findFirst().get();
    }

    private boolean checkIfInvitationIsAcceptedForTournament(List<InvitationRequestDTO> invitations, String tournamentName){
        return invitations.stream().filter(invitationRequestDTO -> invitationRequestDTO.getTournamentName().equals(tournamentName))
                .findFirst().get().isAccepted();
    }

    private boolean containsDuplicates(List<String> values){
        Set<String> setWithoutDuplicates = new HashSet<>(values);
        return setWithoutDuplicates.size() < values.size();
    }

    private void checkIfPlayersCountIsGreaterThanLimit(List<String> tournamentsNamesWithTooManyPlayers, Tournament tournament, String playerName, int slotsCount) {
            Participation participationOfPlayer = tournament.getParticipation().stream()
                .filter(participation -> participation.getPlayer().getName().equals(playerName))
                .findFirst().orElse(null);

            if(participationOfPlayer==null && tournament.getParticipation().size()+slotsCount>=tournament.getMaxPlayers()){
                tournamentsNamesWithTooManyPlayers.add(tournament.getName());
            }
    }

    private void checkIfPlayerAlreadyHaveParticipation(Tournament tournament, Player secondPlayer, String userName, BindingResult bindingResult){
        Participation participationOfSecondPlayer = tournament.getParticipation().stream()
                .filter(participation -> participation.getPlayer().equals(secondPlayer)).findFirst().orElse(null);
        if(participationOfSecondPlayer!=null){
            Participation participationOfEditedPlayer = tournament.getParticipation().stream()
                    .filter(participation -> participation.getPlayer().getName().equals(userName)).findFirst().orElse(null);
            if(participationOfEditedPlayer==null ||  !participationOfEditedPlayer.getGroupNumber().equals(participationOfSecondPlayer.getGroupNumber())){
                bindingResult.rejectValue("participatedTournaments","",
                        new StringBuilder("Player: ").append(secondPlayer.getName()).append(" already participated in tournament: ")
                                .append(tournament.getName()).toString());
            }
        }
    }

    private void reportTournamentsWithTooManyPlayers(List<String> tournamentsNamesWithTooManyPlayers,BindingResult bindingResult){
        StringBuilder raport = new StringBuilder("Tournaments: ")
                .append(tournamentsNamesWithTooManyPlayers.stream().collect(Collectors.joining(", ")))
                .append(" have too many players");
        bindingResult.rejectValue("participatedTournaments","",raport.toString());
    }

    private void reportTournamentsWithTooManyOrganizers(List<String> tournamentsNamesWithTooManyPlayers,BindingResult bindingResult){
        StringBuilder raport = new StringBuilder("Tournaments: ")
                .append(tournamentsNamesWithTooManyPlayers.stream().collect(Collectors.joining(", ")))
                .append(" have too many organizers");
        bindingResult.rejectValue("organizedTournaments","",raport.toString());
    }
}
