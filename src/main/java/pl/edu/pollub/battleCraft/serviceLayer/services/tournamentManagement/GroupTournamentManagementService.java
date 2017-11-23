package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.DuplicatedPlayersNamesException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
public class GroupTournamentManagementService extends TournamentManagementService{

    @Autowired
    public GroupTournamentManagementService(TournamentRepository tournamentRepository) {
        super(tournamentRepository);
    }

    public GroupTournament startTournament(Tournament tournamentInput) {
        GroupTournament tournament = this.castToGroupTournament(tournamentInput);
        this.checkIfTournamentCanStart(tournament);
        this.checkIfTournamentIsNotOutOfDate(tournament);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournament.filterNoAcceptedParticipation();
        if(tournament.getParticipation().size()<4){
            throw new TooFewPlayersToStartTournament(tournament.getName());
        }
        tournament.filterNoAcceptedOrganizations();

        int maxToursCount = this.calculateToursNumber(tournament);
        int battlesCount = this.calculateNumberOfBattles(tournament.getParticipation().size());

        for(int toursNumber=0;toursNumber<maxToursCount;toursNumber++){
            tournament.getTours().add(new Tour(toursNumber,battlesCount,tournament));
        }

        tournament.setToursCount(maxToursCount);
        tournament.setCurrentTourNumber(0);

        return tournamentRepository.save(tournament);
    }

    public GroupTournament setPoints(String tournamentName, GroupBattleRequestDTO battleDTO) {
        if(battleDTO.containsDuplicatedNames())
            throw new DuplicatedPlayersNamesException();

        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(tournamentName));


        if(battleDTO.getTourNumber()>tournament.getCurrentTourNumber())
            throw new EntityNotFoundException(Tour.class,String.valueOf(tournament.getCurrentTourNumber()));

        if(battleDTO.containsEmptyName()){
            tournament.getTourByNumber(battleDTO.getTourNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
            return tournamentRepository.save(tournament);
        }

        if(tournament.getParticipation().size()/2%2!=0 && battleDTO.getTableNumber()==floor(tournament.getParticipation().size() / 4.0f))
            throw new ThisTableIsReservedForAlonePlayer();

        List<Participation> firstPlayersGroupParticipation =
                battleDTO.getFirstPlayersGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
                        .collect(Collectors.toList());

        List<Player> firstPlayersGroup = firstPlayersGroupParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        if(!firstPlayersGroupParticipation.get(0).getGroupNumber().equals(firstPlayersGroupParticipation.get(1).getGroupNumber())){
            throw new ThisPlayersAreNotInTheSameGroup(firstPlayersGroup.get(0).getName(),firstPlayersGroup.get(1).getName());
        }

        List<Participation> secondPlayersGroupParticipation =
                battleDTO.getSecondPlayersGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
                        .collect(Collectors.toList());

        List<Player> secondPlayersGroup = secondPlayersGroupParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        if(!secondPlayersGroupParticipation.get(0).getGroupNumber().equals(secondPlayersGroupParticipation.get(1).getGroupNumber())){
            throw new ThisPlayersAreNotInTheSameGroup(secondPlayersGroup.get(0).getName(),secondPlayersGroup.get(1).getName());
        }

        List<List<Player>> playersWithoutBattle = tournament.getPlayersWithoutBattleInTour(battleDTO.getTourNumber());
        if(playersWithoutBattle.contains(firstPlayersGroup)){
            this.setPointsForPlayers(playersWithoutBattle,firstPlayersGroup,secondPlayersGroup,tournament,battleDTO);
        }
        else{
            int firstPlayerTableNumber = tournament.getTableNumberForPlayer(firstPlayersGroup.get(0),battleDTO.getTourNumber());
            if(firstPlayerTableNumber == battleDTO.getTableNumber()){
                this.setPointsForPlayers(playersWithoutBattle,firstPlayersGroup,secondPlayersGroup,tournament,battleDTO);
            }
            else{
                throw new ThisPlayersHasBattleInThisTour(firstPlayersGroup.get(0).getName(),
                                                         firstPlayersGroup.get(1).getName(),
                                                         tournament.getCurrentTourNumber());
            }
        }

        return tournamentRepository.save(tournament);
    }

    public GroupTournament nextTour(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()+1);
        if (tournament.getCurrentTourNumber() >= tournament.getTours().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<List<Player>> playersGroupsSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersGroupsSortedByPointsFromPreviousTours);
        }
        return tournamentRepository.save(tournament);
    }

    public GroupTournament previousTour(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        if(tournament.getCurrentTourNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTourByNumber(tournament.getCurrentTourNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()-1);
        return tournamentRepository.save(tournament);
    }

    public GroupTournament finishTournament(String name) {
        GroupTournament tournament = this.castToGroupTournament(this.findStartedTournamentByName(name));
        this.checkIfTournamentIsNotOutOfDate(tournament);
        tournament.checkIfAllBattlesAreFinished();
        int indexOfCurrentTour = tournament.getCurrentTourNumber();
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != tournament.getTours().size())
            throw new TournamentCannotBeFinished(tournament.getName());
        tournament.setStatus(TournamentStatus.FINISHED);
        return tournamentRepository.save(tournament);
    }


    private void setPointsForPlayers(List<List<Player>> playersWithoutBattle, List<Player> firstPlayersGroup, List<Player> secondPlayersGroup,
                                    Tournament tournament, GroupBattleRequestDTO battleDTO){
        if(playersWithoutBattle.contains(secondPlayersGroup)){
            tournament.getTourByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
        }
        else{
            int secondPlayerTableNumber = tournament.getTableNumberForPlayer(secondPlayersGroup.get(0),battleDTO.getTourNumber());
            if(secondPlayerTableNumber == battleDTO.getTableNumber()){
                tournament.getTourByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
            }
            else{
                throw new ThisPlayersHasBattleInThisTour(secondPlayersGroup.get(0).getName(),
                        secondPlayersGroup.get(1).getName(),
                        tournament.getCurrentTourNumber());
            }
        }
    }

    private int calculateToursNumber(GroupTournament tournament){
        int maxToursNumber = tournament.getParticipation().size()*2;
        if(maxToursNumber<tournament.getToursCount())
            return (int) (long) maxToursNumber;
        else
            return tournament.getToursCount();
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

    private GroupTournament castToGroupTournament(Tournament tournament){
        Preconditions.checkArgument(tournament.getPlayersOnTableCount() != 4,
                "Invalid type of tournament: %s.", tournament.getTournamentType());
        return (GroupTournament)tournament;
    }
}
