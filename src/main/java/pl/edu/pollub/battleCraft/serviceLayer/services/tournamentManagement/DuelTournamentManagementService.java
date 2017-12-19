package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.DuplicatedPlayersNamesException;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;

import java.util.List;

import static java.lang.Math.floor;

@Service
@Transactional
public class DuelTournamentManagementService extends TournamentManagementService{

    @Autowired
    public DuelTournamentManagementService(TournamentRepository tournamentRepository, AuthorityRecognizer authorityRecognizer) {
        super(tournamentRepository, authorityRecognizer);
    }

    public DuelTournament startTournament(Tournament tournamentInput) {
        DuelTournament tournament = this.castToDuelTournament(tournamentInput);
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
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
            tournament.getTurns().add(new Turn(toursNumber,battlesCount,tournament));
        }

        tournament.setTurnsCount(maxToursCount);
        tournament.setCurrentTurnNumber(0);

        return tournamentRepository.save(tournament);
    }

    public DuelTournament setPoints(String tournamentName, DuelBattleRequestDTO battleDTO) {
        boolean containsEmptyNames = battleDTO.getFirstPlayer().getName().equals("") || battleDTO.getSecondPlayer().getName().equals("");

        if(battleDTO.getSecondPlayer().getName().equals(battleDTO.getFirstPlayer().getName()) && !containsEmptyNames)
            throw new DuplicatedPlayersNamesException();

        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(tournamentName));
        authorityRecognizer.checkIfUserIsAdminOrOrganizerAndCanManageTournament(tournament);

        if(battleDTO.getTourNumber()>tournament.getCurrentTurnNumber())
            throw new ObjectNotFoundException(Turn.class,new StringBuilder(tournament.getCurrentTurnNumber()).toString());

        if(containsEmptyNames){
            tournament.getTurnByNumber(battleDTO.getTourNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
            return tournamentRepository.save(tournament);
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
                throw new ThisPlayerHasBattleInThisTurn(firstPlayer.getName(),tournament.getCurrentTurnNumber());
            }
        }

        return tournamentRepository.save(tournament);
    }

    public DuelTournament nextTurn(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTurnNumber(tournament.getCurrentTurnNumber()+1);
        if (tournament.getCurrentTurnNumber() >= tournament.getTurns().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<Player> playersSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersSortedByPointsFromPreviousTours);
        }
        return tournamentRepository.save(tournament);
    }

    public DuelTournament previousTurn(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        if(tournament.getCurrentTurnNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTurnByNumber(tournament.getCurrentTurnNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTurnNumber(tournament.getCurrentTurnNumber()-1);
        return tournamentRepository.save(tournament);
    }

    public DuelTournament finishTournament(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
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

    public DuelTournament castToDuelTournament(Tournament tournament){
        Preconditions.checkArgument(tournament.getPlayersOnTableCount() == 2,
                "Invalid type of tournament: %s.", tournament.getTournamentType());
        return (DuelTournament)tournament;
    }

    private void setPointsForPlayers(List<Player> playersWithoutBattle, Player firstPlayer, Player secondPlayer,
                                    Tournament tournament, DuelBattleRequestDTO battleDTO){
        if(playersWithoutBattle.contains(secondPlayer)){
            tournament.getTurnByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayer,secondPlayer);
        }
        else{
            int secondPlayerTableNumber = tournament.getTableNumberForPlayer(secondPlayer,battleDTO.getTourNumber());
            if(secondPlayerTableNumber == battleDTO.getTableNumber()){
                tournament.getTurnByNumber(battleDTO.getTourNumber()).setPoints(battleDTO,firstPlayer,secondPlayer);
            }
            else{

                throw new ThisPlayerHasBattleInThisTurn(secondPlayer.getName(),tournament.getCurrentTurnNumber());
            }
        }
    }

    private int calculateToursNumber(DuelTournament tournament){
        int maxToursNumber = tournament.getParticipation().size()*2;
        if(maxToursNumber<tournament.getTurnsCount())
            return (int) (long) maxToursNumber;
        else
            return tournament.getTurnsCount();
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

}
