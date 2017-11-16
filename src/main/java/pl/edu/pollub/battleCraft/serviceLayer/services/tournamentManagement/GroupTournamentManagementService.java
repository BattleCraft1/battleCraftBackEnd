package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

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
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.TournamentProgress.GroupTournamentProgressDTOMapper;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.GroupTournamentProgressResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupTournamentManagementService {

    private final TournamentRepository tournamentRepository;

    private final GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper;

    @Autowired
    public GroupTournamentManagementService(TournamentRepository tournamentRepository, GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper) {
        this.tournamentRepository = tournamentRepository;
        this.groupTournamentProgressDTOMapper = groupTournamentProgressDTOMapper;
    }

    public GroupTournamentProgressResponseDTO startTournament(String tournamentName) {
        GroupTournament tournament = this.findNotStartedTournamentByName(tournamentName);
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

        return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public GroupTournamentProgressResponseDTO setPoints(String tournamentName, GroupBattleRequestDTO battleDTO) {
        if(battleDTO.containsDuplicatedNames())
            throw new DuplicatedPlayersNamesException();

        GroupTournament tournament = this.findStartedTournamentByName(tournamentName);

        if(battleDTO.getTourNumber()>tournament.getCurrentTourNumber())
            throw new EntityNotFoundException(Tour.class,String.valueOf(tournament.getCurrentTourNumber()));

        if(battleDTO.containsEmptyName()){
            tournament.getTourByNumber(battleDTO.getTourNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
            return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
        }

        List<Participation> firstPlayersGroupParticipation =
                battleDTO.getFirstPlayersGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
                        .collect(Collectors.toList());

        List<Player> firstPlayersGroup = firstPlayersGroupParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        if(!firstPlayersGroupParticipation.get(0).getGroupNumber().equals(firstPlayersGroupParticipation.get(1).getGroupNumber())){
            throw new ThisPlayersAreNotInTheSameGroup(firstPlayersGroup.get(0).getName(),firstPlayersGroup.get(1).getName());
        }

        List<Participation> secondPlayersGroupParticipation =
                battleDTO.getSecondPlayerGroup().getPlayersNames().stream().map(tournament::getParticipationByPlayerName)
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
            int firstPlayerTableNumber = tournament.getTableNumberForPlayer(firstPlayersGroup.get(0));
            if(firstPlayerTableNumber == battleDTO.getTableNumber()){
                this.setPointsForPlayers(playersWithoutBattle,firstPlayersGroup,secondPlayersGroup,tournament,battleDTO);
            }
            else{
                throw new ThisPlayersHasBattleInThisTour(firstPlayersGroup.get(0).getName(),
                                                         firstPlayersGroup.get(1).getName(),
                                                         tournament.getCurrentTourNumber());
            }
        }

        tournament.getTourByNumber(tournament.getCurrentTourNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
        return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public GroupTournamentProgressResponseDTO getTournamentProgress(String name) {
        Tournament tournament = Optional.ofNullable(tournamentRepository.findNotNewTournamentByUniqueName(name))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,name));
        if(tournament.getPlayersOnTableCount() == 4)
            return groupTournamentProgressDTOMapper.map((GroupTournament) tournament);
        else
            throw new InvalidTypeOfTournament();
    }

    public GroupTournamentProgressResponseDTO nextTour(String name) {
        GroupTournament tournament = this.findStartedTournamentByName(name);
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()+1);
        if (tournament.getCurrentTourNumber() >= tournament.getTours().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<List<Player>> playersGroupsSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersGroupsSortedByPointsFromPreviousTours);
        }
        return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public GroupTournamentProgressResponseDTO previousTour(String name) {
        GroupTournament tournament = this.findStartedTournamentByName(name);
        if(tournament.getCurrentTourNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTourByNumber(tournament.getCurrentTourNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()-1);
        return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public GroupTournamentProgressResponseDTO finishTournament(String name) {
        GroupTournament tournament = this.findStartedTournamentByName(name);
        this.checkIfTournamentIsNotOutOfDate(tournament);
        tournament.checkIfAllBattlesAreFinished();
        int indexOfCurrentTour = tournament.getCurrentTourNumber();
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != tournament.getTours().size())
            throw new TournamentCannotBeFinished(tournament.getName());
        tournament.setStatus(TournamentStatus.FINISHED);
        return groupTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }


    private void setPointsForPlayers(List<List<Player>> playersWithoutBattle, List<Player> firstPlayersGroup, List<Player> secondPlayersGroup,
                                    Tournament tournament, GroupBattleRequestDTO battleDTO){
        if(playersWithoutBattle.contains(secondPlayersGroup)){
            tournament.getTourByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayersGroup,secondPlayersGroup);
        }
        else{
            int secondPlayerTableNumber = tournament.getTableNumberForPlayer(secondPlayersGroup.get(0));
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

    private void checkIfTournamentCanStart(Tournament tournament){
        if(tournament.getDateOfStart().before(new Date())){
            throw new TournamentCannotStartYet(tournament.getName(),tournament.getDateOfStart());
        }
    }

    private void checkIfTournamentIsNotOutOfDate(Tournament tournament){
        if((new Date()).after(tournament.getDateOfEnd())){
            tournament.setBanned(true);
            tournamentRepository.save(tournament);
            throw new TournamentIsOutOfDate(tournament.getName(),tournament.getDateOfEnd());
        }
    }

    private GroupTournament findNotStartedTournamentByName(String tournamentName){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentName));
        if(tournament.getStatus()!=TournamentStatus.ACCEPTED)
            throw new TournamentNotAccepted(tournament.getName());
        if(tournament.getPlayersOnTableCount() == 4)
            return (GroupTournament)tournament;
        else
            throw new InvalidTypeOfTournament();
    }

    private GroupTournament findStartedTournamentByName(String tournamentName){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findStartedTournamentByUniqueName(tournamentName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentName));
        if(tournament.getPlayersOnTableCount() == 4)
            return (GroupTournament)tournament;
        else
            throw new InvalidTypeOfTournament();
    }
}
