package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsNotAcceptedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.DuplicatedPlayersNamesException;
import pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.TournamentProgress.DuelTournamentProgressDTOMapper;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.DuelTournamentProgressResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.floor;

@Service
public class DuelTournamentManagementService {

    private final TournamentRepository tournamentRepository;

    private final DuelTournamentProgressDTOMapper duelTournamentProgressDTOMapper;

    @Autowired
    public DuelTournamentManagementService(TournamentRepository tournamentRepository, DuelTournamentProgressDTOMapper duelTournamentProgressDTOMapper) {
        this.tournamentRepository = tournamentRepository;
        this.duelTournamentProgressDTOMapper = duelTournamentProgressDTOMapper;
    }

    public DuelTournamentProgressResponseDTO getTournamentProgress(DuelTournament tournament) {
        if(tournament.getPlayersOnTableCount() == 2){
            if(tournament.isBanned()){
                throw new ThisObjectIsBannedException(Tournament.class,tournament.getName());
            }
            else if(tournament.getStatus()==TournamentStatus.FINISHED || tournament.getStatus()==TournamentStatus.IN_PROGRESS){
                return duelTournamentProgressDTOMapper.map((DuelTournament)tournament);
            }
            else if(tournament.getStatus()==TournamentStatus.ACCEPTED){
                return this.startTournament(tournament);
            }
            else{
                throw new ThisObjectIsNotAcceptedException(Tournament.class,tournament.getName());
            }
        }
        else
            throw new InvalidTypeOfTournament();
    }

    private DuelTournamentProgressResponseDTO startTournament(DuelTournament tournament) {
        this.checkIfTournamentCanStart(tournament);
        this.checkIfTournamentIsNotOutOfDate(tournament);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournament.filterNoAcceptedParticipation();
        if(tournament.getParticipation().size()<2){
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

        return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public DuelTournamentProgressResponseDTO setPoints(String tournamentName, DuelBattleRequestDTO battleDTO) {
        boolean containsEmptyNames = battleDTO.getFirstPlayer().getName().equals("") || battleDTO.getSecondPlayer().getName().equals("");
        if(battleDTO.getSecondPlayer().getName().equals(battleDTO.getFirstPlayer().getName()) && !containsEmptyNames)
            throw new DuplicatedPlayersNamesException();

        DuelTournament tournament = this.findStartedTournamentByName(tournamentName);

        if(battleDTO.getTourNumber()>tournament.getCurrentTourNumber())
            throw new EntityNotFoundException(Tour.class,new StringBuilder(tournament.getCurrentTourNumber()).toString());

        if(containsEmptyNames){
            tournament.getTourByNumber(battleDTO.getTourNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
            return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
        }

        if(tournament.getParticipation().size()%2!=0 && battleDTO.getTableNumber()==floor(tournament.getParticipation().size() / 2.0f))
            throw new ThisTableIsReservedForAlonePlayer();

        Player firstPlayer = tournament.getPlayerByName(battleDTO.getFirstPlayer().getName());

        Player secondPlayer = tournament.getPlayerByName(battleDTO.getSecondPlayer().getName());

        List<Player> playersWithoutBattle = tournament.getPlayersWithoutBattleInTour(battleDTO.getTourNumber());
        if(playersWithoutBattle.contains(firstPlayer)){
            this.setPointsForPlayers(playersWithoutBattle,firstPlayer,secondPlayer,tournament,battleDTO);
        }
        else{
            int firstPlayerTableNumber = tournament.getTableNumberForPlayer(firstPlayer,battleDTO.getTourNumber());
            if(firstPlayerTableNumber == battleDTO.getTableNumber()){
                this.setPointsForPlayers(playersWithoutBattle,firstPlayer,secondPlayer,tournament,battleDTO);
            }
            else{
                throw new ThisPlayerHasBattleInThisTour(firstPlayer.getName(),tournament.getCurrentTourNumber());
            }
        }

        return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public DuelTournamentProgressResponseDTO nextTour(String name) {
        DuelTournament tournament = this.findStartedTournamentByName(name);
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()+1);
        if (tournament.getCurrentTourNumber() >= tournament.getTours().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<Player> playersSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersSortedByPointsFromPreviousTours);
        }
        return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public DuelTournamentProgressResponseDTO previousTour(String name) {
        DuelTournament tournament = this.findStartedTournamentByName(name);
        if(tournament.getCurrentTourNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTourByNumber(tournament.getCurrentTourNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()-1);
        return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    public DuelTournamentProgressResponseDTO finishTournament(String name) {
        DuelTournament tournament = this.findStartedTournamentByName(name);
        this.checkIfTournamentIsNotOutOfDate(tournament);
        tournament.checkIfAllBattlesAreFinished();
        int indexOfCurrentTour = tournament.getCurrentTourNumber();
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != tournament.getTours().size())
            throw new TournamentCannotBeFinished(tournament.getName());
        tournament.setStatus(TournamentStatus.FINISHED);
        return duelTournamentProgressDTOMapper.map(tournamentRepository.save(tournament));
    }

    private void setPointsForPlayers(List<Player> playersWithoutBattle, Player firstPlayer, Player secondPlayer,
                                    Tournament tournament, DuelBattleRequestDTO battleDTO){
        if(playersWithoutBattle.contains(secondPlayer)){
            tournament.getTourByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayer,secondPlayer);
        }
        else{
            int secondPlayerTableNumber = tournament.getTableNumberForPlayer(secondPlayer,battleDTO.getTourNumber());
            if(secondPlayerTableNumber == battleDTO.getTableNumber()){
                tournament.getTourByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayer,secondPlayer);
            }
            else{

                throw new ThisPlayerHasBattleInThisTour(secondPlayer.getName(),tournament.getCurrentTourNumber());
            }
        }
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

    private int calculateToursNumber(DuelTournament tournament){
        int maxToursNumber = tournament.getParticipation().size()*2;
        if(maxToursNumber<tournament.getToursCount())
            return (int) (long) maxToursNumber;
        else
            return tournament.getToursCount();
    }

    private int calculateNumberOfBattles(int number){
        if(number%2==0){
            return number/2;
        }
        else{
            return this.calculateNumberOfBattlesWithOneAlonePlayer(number);
        }
    }

    private int calculateNumberOfBattlesWithOneAlonePlayer(int number){
        return number/2+1;
    }

    private DuelTournament findNotStartedTournamentByName(String tournamentName){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentName));
        if(tournament.getStatus()!=TournamentStatus.ACCEPTED)
            throw new TournamentNotAccepted(tournament.getName());
        if(tournament.getPlayersOnTableCount() == 2)
            return (DuelTournament)tournament;
        else
            throw new InvalidTypeOfTournament();
    }

    private DuelTournament findStartedTournamentByName(String tournamentName){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findStartedTournamentByUniqueName(tournamentName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentName));
        if(tournament.getPlayersOnTableCount() == 2)
            return (DuelTournament)tournament;
        else
            throw new InvalidTypeOfTournament();
    }
}
