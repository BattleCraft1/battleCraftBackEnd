package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentProgress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.emuns.ColorOfSideInBattle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.Battle.DuelBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.DuelTournamentProgressResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuelTournamentProgressDTOMapper {

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public DuelTournamentProgressDTOMapper(AuthorityRecognizer authorityRecognizer) {
        this.authorityRecognizer = authorityRecognizer;
    }

    public DuelTournamentProgressResponseDTO map(DuelTournament tournament){
        DuelTournamentProgressResponseDTO duelTournamentProgressDTO = new DuelTournamentProgressResponseDTO();
        List<List<DuelBattleResponseDTO>> toursOfDTO = new ArrayList<>();
        tournament.getTours().forEach(
                tour -> {
                    List<DuelBattleResponseDTO> battlesOfDTO = new ArrayList<>();
                    tour.getBattles().forEach(battle -> {
                        List<Play> plays = battle.getPlayers();
                        List<Play> firstPlayersGroupPlays = plays.stream()
                                .filter(play -> play.getColorOfSideInBattle() == ColorOfSideInBattle.BLUE)
                                .collect(Collectors.toList());
                        List<Play> secondPlayersGroupPlays = plays.stream()
                                .filter(play -> play.getColorOfSideInBattle() == ColorOfSideInBattle.RED)
                                .collect(Collectors.toList());
                        battlesOfDTO.add(
                                new DuelBattleResponseDTO(
                                        battle.getTableNumber(),
                                        new PlayerDTO(
                                                firstPlayersGroupPlays.size()>0?firstPlayersGroupPlays.get(0).getPlayer().getName():"",
                                                firstPlayersGroupPlays.size()>0?firstPlayersGroupPlays.get(0).getPoints():0
                                        ),
                                        new PlayerDTO(
                                                secondPlayersGroupPlays.size()>0?secondPlayersGroupPlays.get(0).getPlayer().getName():"",
                                                secondPlayersGroupPlays.size()>0?secondPlayersGroupPlays.get(0).getPoints():0
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

        List<String> organizersNames = tournament.getOrganizations().stream().map(organization -> organization.getOrganizer().getName()).collect(Collectors.toList());
        duelTournamentProgressDTO.setCanCurrentUserMenageTournament(organizersNames.contains(authorityRecognizer.getCurrentUserNameFromContext()));

        return duelTournamentProgressDTO;
    }
}
