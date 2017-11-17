package pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.TournamentProgress;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.Battle.DuelBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.DuelTournamentProgressResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuelTournamentProgressDTOMapper {
    public DuelTournamentProgressResponseDTO map(DuelTournament tournament){
        DuelTournamentProgressResponseDTO duelTournamentProgressDTO = new DuelTournamentProgressResponseDTO();
        List<List<DuelBattleResponseDTO>> toursOfDTO = new ArrayList<>();
        tournament.getTours().forEach(
                tour -> {
                    List<DuelBattleResponseDTO> battlesOfDTO = new ArrayList<>();
                    tour.getBattles().forEach(battle -> {
                        battlesOfDTO.add(
                                new DuelBattleResponseDTO(
                                        battle.getTableNumber(),
                                        new PlayerDTO(
                                                battle.getPlayers().size()>0?battle.getPlayers().get(0).getPlayer().getName():"",
                                                battle.getPlayers().size()>0?battle.getPlayers().get(0).getPoints():0
                                        ),
                                        new PlayerDTO(
                                                battle.getPlayers().size()>1?battle.getPlayers().get(1).getPlayer().getName():"",
                                                battle.getPlayers().size()>1?battle.getPlayers().get(1).getPoints():0
                                        ),
                                        battle.isFinished()
                                )
                        );
                    });
                    toursOfDTO.add(battlesOfDTO);
                }
        );
        duelTournamentProgressDTO.setTours(toursOfDTO);
        duelTournamentProgressDTO.setPlayersNamesWithPoints(tournament.getParticipation().stream()
                .map(Participation::getPlayer).collect(Collectors.toMap(Player::getName,tournament::getPointsForPlayer)));
        duelTournamentProgressDTO.setPlayersWithoutBattles(tournament.getActivatedTours().stream()
                .collect(Collectors.toMap(Tour::getNumber,
                        tour->tournament.getPlayersWithoutBattleInTour(tour.getNumber()).stream()
                                .map(UserAccount::getName).collect(Collectors.toList()))));
        duelTournamentProgressDTO.setCurrentTourNumber(tournament.getCurrentTourNumber());
        duelTournamentProgressDTO.setTournamentStatus(tournament.getStatus());
        duelTournamentProgressDTO.setPlayersCount(tournament.getParticipation().size());
        return duelTournamentProgressDTO;
    }
}
