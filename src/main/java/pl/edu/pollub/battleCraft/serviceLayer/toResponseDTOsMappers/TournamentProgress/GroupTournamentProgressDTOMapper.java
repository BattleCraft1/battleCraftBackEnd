package pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.TournamentProgress;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayersGroupDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Battle.GroupBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.GroupTournamentProgressResponseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupTournamentProgressDTOMapper {
    public GroupTournamentProgressResponseDTO map(GroupTournament tournament) {
        GroupTournamentProgressResponseDTO groupTournamentProgressResponseDTO = new GroupTournamentProgressResponseDTO();
        List<List<GroupBattleResponseDTO>> toursOfDTO = new ArrayList<>();
        tournament.getTours().forEach(
                tour -> {
                    List<GroupBattleResponseDTO> battlesOfDTO = new ArrayList<>();
                    tour.getBattles().forEach(battle -> {
                                battlesOfDTO.add(
                                        new GroupBattleResponseDTO(
                                            battle.getTableNumber(),
                                                new PlayersGroupDTO(
                                                        battle.getPlayers().size()>0?
                                                                Arrays.asList(
                                                                        battle.getPlayers().get(0).getPlayer().getName(),
                                                                        battle.getPlayers().get(1).getPlayer().getName()
                                                                ):
                                                                Arrays.asList("",""),
                                                        battle.getPlayers().size()>0?battle.getPlayers().get(0).getPoints():0
                                                ),
                                                new PlayersGroupDTO(
                                                        battle.getPlayers().size()>2?
                                                                Arrays.asList(
                                                                        battle.getPlayers().get(2).getPlayer().getName(),
                                                                        battle.getPlayers().get(3).getPlayer().getName()
                                                                ):
                                                                Arrays.asList("",""),
                                                        battle.getPlayers().size()>2?battle.getPlayers().get(2).getPoints():0
                                                )
                                        )
                                );
                            }
                    );
                    toursOfDTO.add(battlesOfDTO);
                }
        );
        groupTournamentProgressResponseDTO.setTours(toursOfDTO);
        groupTournamentProgressResponseDTO.setPlayersNamesWithPoints(tournament.getParticipation().stream()
                .map(Participation::getPlayer).collect(Collectors.toMap(Player::getName,tournament::getPointsForPlayerFromPreviousTours)));
        groupTournamentProgressResponseDTO.setPlayersWithoutBattles(
                tournament.getActivatedTours().stream()
                .collect(Collectors.toMap(
                        Tour::getNumber,
                        tour->tournament.getPlayersWithoutBattleInTour(tour.getNumber()).stream()
                                .map(users -> new ArrayList<String>(){{
                                    addAll(Arrays.asList(users.get(0).getName(),users.get(1).getName()));
                                }})
                                .collect(Collectors.toList()))));
        groupTournamentProgressResponseDTO.setCurrentTourNumber(tournament.getCurrentTourNumber());
        groupTournamentProgressResponseDTO.setTournamentStatus(tournament.getStatus());
        return groupTournamentProgressResponseDTO;
    }
}
