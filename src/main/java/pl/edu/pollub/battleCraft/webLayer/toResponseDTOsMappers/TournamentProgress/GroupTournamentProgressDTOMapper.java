package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentProgress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.emuns.ColorOfSideInBattle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayersGroupDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Battle.GroupBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.GroupTournamentProgressResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Points.PlayersGroupWithPointsDTO;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GroupTournamentProgressDTOMapper {

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public GroupTournamentProgressDTOMapper(AuthorityRecognizer authorityRecognizer) {
        this.authorityRecognizer = authorityRecognizer;
    }

    public GroupTournamentProgressResponseDTO map(GroupTournament tournament) {
        GroupTournamentProgressResponseDTO groupTournamentProgressResponseDTO = new GroupTournamentProgressResponseDTO();

        List<List<GroupBattleResponseDTO>> toursOfDTO = new ArrayList<>();
        tournament.getTours().forEach(
                tour -> {
                    List<GroupBattleResponseDTO> battlesOfDTO = new ArrayList<>();
                    tour.getBattles().forEach(battle -> {
                        List<Play> plays = battle.getPlayers();
                        List<Play> firstPlayersGroupPlays = plays.stream()
                                .filter(play -> play.getColorOfSideInBattle() == ColorOfSideInBattle.BLUE)
                                .collect(Collectors.toList());
                        List<Play> secondPlayersGroupPlays = plays.stream()
                                .filter(play -> play.getColorOfSideInBattle() == ColorOfSideInBattle.RED)
                                .collect(Collectors.toList());

                                battlesOfDTO.add(
                                        new GroupBattleResponseDTO(
                                            battle.getTableNumber(),
                                                new PlayersGroupDTO(
                                                        firstPlayersGroupPlays.size()>=2?
                                                                Arrays.asList(
                                                                        firstPlayersGroupPlays.get(0).getPlayer().getName(),
                                                                        firstPlayersGroupPlays.get(1).getPlayer().getName()
                                                                ): Arrays.asList("",""),
                                                        firstPlayersGroupPlays.size()>=2?firstPlayersGroupPlays.get(0).getPoints():0
                                                ),
                                                new PlayersGroupDTO(
                                                        secondPlayersGroupPlays.size()>=2?
                                                                Arrays.asList(
                                                                        secondPlayersGroupPlays.get(0).getPlayer().getName(),
                                                                        secondPlayersGroupPlays.get(1).getPlayer().getName()
                                                                ): Arrays.asList("",""),
                                                        secondPlayersGroupPlays.size()>=2?secondPlayersGroupPlays.get(0).getPoints():0
                                                ),
                                                battle.isFinished()
                                        )
                                );
                            }
                    );
                    toursOfDTO.add(battlesOfDTO);
                }
        );
        groupTournamentProgressResponseDTO.setTours(toursOfDTO);

        groupTournamentProgressResponseDTO.setPlayersNamesWithPoints(tournament.getGroupedPlayers().stream()
                .map(playersGroup -> new PlayersGroupWithPointsDTO(
                        Arrays.asList(playersGroup.get(0).getName(),playersGroup.get(1).getName()),
                        tournament.getPointsForPlayer(playersGroup.get(0))))
                .collect(Collectors.toList()));

        groupTournamentProgressResponseDTO.setPlayersWithoutBattles(
                tournament.getActivatedTours().stream()
                .collect(Collectors.toMap(
                        Tour::getNumber,
                        tour->tournament.getPlayersWithoutBattleInTour(tour.getNumber()).stream()
                                .map(users -> Arrays.asList(users.get(0).getName(),users.get(1).getName()))
                                .collect(Collectors.toList()))));

        groupTournamentProgressResponseDTO.setCurrentTourNumber(tournament.getCurrentTourNumber());
        groupTournamentProgressResponseDTO.setTournamentStatus(tournament.getStatus());
        groupTournamentProgressResponseDTO.setPlayersCount(tournament.getParticipation().size()/2);

        List<String> organizersNames = tournament.getOrganizations().stream().map(organization -> organization.getOrganizer().getName()).collect(Collectors.toList());
        groupTournamentProgressResponseDTO.setCanCurrentUserMenageTournament(
                (organizersNames.contains(authorityRecognizer.getCurrentUserNameFromContext())
                        && tournament.getStatus()!= TournamentStatus.FINISHED)
                || authorityRecognizer.getCurrentUserRoleFromContext().equals("ROLE_ADMIN")
        );

        return groupTournamentProgressResponseDTO;
    }
}
