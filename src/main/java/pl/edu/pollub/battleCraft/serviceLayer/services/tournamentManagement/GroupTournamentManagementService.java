package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.DuplicatedPlayersNamesException;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
@Transactional
public class GroupTournamentManagementService extends TournamentManagementService{

    @Autowired
    public GroupTournamentManagementService(TournamentRepository tournamentRepository, AuthorityRecognizer authorityRecognizer) {
        super(tournamentRepository, authorityRecognizer);
    }

    public GroupTournament startTournament(Tournament tournamentInput) {
        GroupTournament tournament = this.castToGroupTournament(tournamentInput);
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        this.checkIfTournamentCanStart(tournament);
        this.checkIfTournamentIsNotOutOfDate(tournament);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournament.filterNoAcceptedParticipation();
        if(tournament.getParticipation().size()<4){
            throw new TooFewPlayersToStartTournament(tournament.getName());
        }
        tournament.filterNoAcceptedOrganizations();
        if(tournament.getOrganizations().size()<1){
            throw new TooFewOrganizersToStartTournament(tournament.getName());
        }

        int maxTurnsCount = this.calculateTurnsNumber(tournament);
        int battlesCount = this.calculateNumberOfBattles(tournament.getParticipation().size());

        for(int turnsNumber=0;turnsNumber<maxTurnsCount;turnsNumber++){
            tournament.getTurns().add(new Turn(turnsNumber,battlesCount,tournament));
        }

        tournament.setTurnsCount(maxTurnsCount);
        tournament.setCurrentTurnNumber(0);

        return tournamentRepository.save(tournament);
    }

    public GroupTournament setPoints(String tournamentName, GroupBattleRequestDTO battleDTO) {
        if(battleDTO.containsDuplicatedNames())
            throw new DuplicatedPlayersNamesException();

        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(tournamentName));
        authorityRecognizer.checkIfUserIsAdminOrOrganizerAndCanManageTournament(tournament);

        if(battleDTO.getTurnNumber()>tournament.getCurrentTurnNumber())
            throw new ObjectNotFoundException(Turn.class,String.valueOf(tournament.getCurrentTurnNumber()));

        if(battleDTO.containsEmptyName()){
            tournament.getTurnByNumber(battleDTO.getTurnNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
            return tournamentRepository.save(tournament);
        }

        if(tournament.getParticipation().size()/2%2!=0 && battleDTO.getTableNumber()==floor(tournament.getParticipation().size() / 4.0f))
            throw new ThisTableIsReservedForAlonePlayer();

        List<Participation> firstPlayersGroupParticipation =
                battleDTO.getFirstPlayersGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
                        .collect(Collectors.toList());

        List<Player> firstPlayersGroup = firstPlayersGroupParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        if(!ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(firstPlayersGroupParticipation.get(0),firstPlayersGroupParticipation.get(1))){
            throw new ThisPlayersAreNotInTheSameGroup(firstPlayersGroup.get(0).getName(),firstPlayersGroup.get(1).getName());
        }

        List<Participation> secondPlayersGroupParticipation =
                battleDTO.getSecondPlayersGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
                        .collect(Collectors.toList());

        List<Player> secondPlayersGroup = secondPlayersGroupParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        if(!ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(secondPlayersGroupParticipation.get(0),secondPlayersGroupParticipation.get(1))){
            throw new ThisPlayersAreNotInTheSameGroup(secondPlayersGroup.get(0).getName(),secondPlayersGroup.get(1).getName());
        }

        List<List<Player>> playersWithoutBattle = tournament.getPlayersWithoutBattleInTour(battleDTO.getTurnNumber());
        if(playersWithoutBattle.contains(firstPlayersGroup)){
            this.setPointsForPlayers(playersWithoutBattle,firstPlayersGroup,secondPlayersGroup,tournament,battleDTO);
        }
        else{
            int firstPlayerTableNumber = tournament.getTableNumberForPlayer(firstPlayersGroup.get(0),battleDTO.getTurnNumber());
            if(firstPlayerTableNumber == battleDTO.getTableNumber()){
                this.setPointsForPlayers(playersWithoutBattle,firstPlayersGroup,secondPlayersGroup,tournament,battleDTO);
            }
            else{
                throw new ThisPlayersHasBattleInThisTurn(firstPlayersGroup.get(0).getName(),
                                                         firstPlayersGroup.get(1).getName(),
                                                         tournament.getCurrentTurnNumber());
            }
        }

        return tournamentRepository.save(tournament);
    }

    public GroupTournament nextTurn(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTurnNumber(tournament.getCurrentTurnNumber()+1);
        if (tournament.getCurrentTurnNumber() >= tournament.getTurns().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<List<Player>> playersGroupsSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersGroupsSortedByPointsFromPreviousTours);
        }
        return tournamentRepository.save(tournament);
    }

    public GroupTournament previousTurn(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        if(tournament.getCurrentTurnNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTurnByNumber(tournament.getCurrentTurnNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTurnNumber(tournament.getCurrentTurnNumber()-1);
        return tournamentRepository.save(tournament);
    }

    public GroupTournament finishTournament(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        this.checkIfTournamentIsNotOutOfDate(tournament);
        tournament.checkIfAllBattlesAreFinished();
        int indexOfCurrentTour = tournament.getCurrentTurnNumber();
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != tournament.getTurns().size())
            throw new TournamentCannotBeFinished(tournament.getName());
        tournament.setStatus(TournamentStatus.FINISHED);
        this.advancePlayersToOrganizers(tournament);
        return tournamentRepository.save(tournament);
    }

    public GroupTournament castToGroupTournament(Tournament tournament){
        Preconditions.checkArgument(tournament.getPlayersOnTableCount() == 4,
                "Invalid type of tournament: %s.", tournament.getTournamentType());
        return (GroupTournament)tournament;
    }

    private void setPointsForPlayers(List<List<Player>> playersWithoutBattle, List<Player> firstPlayersGroup, List<Player> secondPlayersGroup,
                                    Tournament tournament, GroupBattleRequestDTO battleDTO){
        if(playersWithoutBattle.contains(secondPlayersGroup)){
            tournament.getTurnByNumber(battleDTO.getTurnNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
        }
        else{
            int secondPlayerTableNumber = tournament.getTableNumberForPlayer(secondPlayersGroup.get(0),battleDTO.getTurnNumber());
            if(secondPlayerTableNumber == battleDTO.getTableNumber()){
                tournament.getTurnByNumber(battleDTO.getTurnNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
            }
            else{
                throw new ThisPlayersHasBattleInThisTurn(secondPlayersGroup.get(0).getName(),
                        secondPlayersGroup.get(1).getName(),
                        tournament.getCurrentTurnNumber());
            }
        }
    }

    private int calculateTurnsNumber(GroupTournament tournament){
        int maxToursNumber = tournament.getParticipation().size()*2;
        if(maxToursNumber<tournament.getTurnsCount())
            return (int) (long) maxToursNumber;
        else
            return tournament.getTurnsCount();
    }

    private int calculateNumberOfBattles(int number){
        if(number/2%2==0){
            return number/4;
        }
        else{
            return this.calculateNumberOfBattlesWithOneAlonePlayer(number);
        }
    }

    private int calculateNumberOfBattlesWithOneAlonePlayer(int number){
        return number/4+1;
    }

}
