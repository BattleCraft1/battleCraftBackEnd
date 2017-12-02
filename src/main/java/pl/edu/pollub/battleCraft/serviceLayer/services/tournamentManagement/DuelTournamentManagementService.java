package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers.PlayerToOrganizerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.DuplicatedPlayersNamesException;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;

import java.util.List;

import static java.lang.Math.floor;

@Service
public class DuelTournamentManagementService extends TournamentManagementService{

    @Autowired
    public DuelTournamentManagementService(TournamentRepository tournamentRepository, PlayerToOrganizerMapper playerToOrganizerMapper,
                                           OrganizerRepository organizerRepository, AuthorityRecognizer authorityRecognizer) {
        super(tournamentRepository, authorityRecognizer, playerToOrganizerMapper, organizerRepository);
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
            tournament.getTours().add(new Tour(toursNumber,battlesCount,tournament));
        }

        tournament.setToursCount(maxToursCount);
        tournament.setCurrentTourNumber(0);

        return tournamentRepository.save(tournament);
    }

    public DuelTournament setPoints(String tournamentName, DuelBattleRequestDTO battleDTO) {
        boolean containsEmptyNames = battleDTO.getFirstPlayer().getName().equals("") || battleDTO.getSecondPlayer().getName().equals("");

        if(battleDTO.getSecondPlayer().getName().equals(battleDTO.getFirstPlayer().getName()) && !containsEmptyNames)
            throw new DuplicatedPlayersNamesException();

        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(tournamentName));
        authorityRecognizer.checkIfUserIsAdminOrOrganizerAndCanManageTournament(tournament);

        if(battleDTO.getTourNumber()>tournament.getCurrentTourNumber())
            throw new ObjectNotFoundException(Tour.class,new StringBuilder(tournament.getCurrentTourNumber()).toString());

        if(containsEmptyNames){
            tournament.getTourByNumber(battleDTO.getTourNumber()).findBattleByTableNumber(battleDTO.getTableNumber()).clearPlayers();
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
                throw new ThisPlayerHasBattleInThisTour(firstPlayer.getName(),tournament.getCurrentTourNumber());
            }
        }

        return tournamentRepository.save(tournament);
    }

    public DuelTournament nextTour(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        tournament.checkIfAllBattlesAreFinished();
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()+1);
        if (tournament.getCurrentTourNumber() >= tournament.getTours().size()){
            throw new TournamentIsFinished(tournament.getName());
        }
        else{
            List<Player> playersSortedByPointsFromPreviousTours = tournament.sortPlayersByPointsFromPreviousTours();
            tournament.pairPlayersInNextTour(playersSortedByPointsFromPreviousTours);
        }
        return tournamentRepository.save(tournament);
    }

    public DuelTournament previousTour(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        if(tournament.getCurrentTourNumber() <= 0)
            throw new ItIsFirstTourOfTournament(name);
        tournament.getTourByNumber(tournament.getCurrentTourNumber()).getBattles().forEach(Battle::clearPlayers);
        tournament.setCurrentTourNumber(tournament.getCurrentTourNumber()-1);
        return tournamentRepository.save(tournament);
    }

    public DuelTournament finishTournament(String name) {
        DuelTournament tournament = this.castToDuelTournament(this.findStartedTournamentByName(name));
        authorityRecognizer.checkIfUserCanManageTournament(tournament);
        this.checkIfTournamentIsNotOutOfDate(tournament);
        tournament.checkIfAllBattlesAreFinished();
        int indexOfCurrentTour = tournament.getCurrentTourNumber();
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != tournament.getTours().size())
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

}
