package pl.edu.pollub.battleCraft.dataLayer.domain.Tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.BattleOnTableNotFinishedYet;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.NotValidPointsNumber;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Tour {

    public Tour(int number,int battlesCount, Tournament tournament){
        this.number = number;
        this.tournament = tournament;
        for(int battleNumber=0;battleNumber<battlesCount;battleNumber++){
            battles.add(new Battle(this,battleNumber));
        }
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int number;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tour")
    private List<Battle> battles = new ArrayList<>();

    public List<Player> getAllPlayersInTour(){
        return this.getBattles().stream()
                .flatMap(battle -> battle.getPlayers().stream())
                .map(Play::getPlayer).collect(Collectors.toList());
    }

    public void setPoints(DuelBattleRequestDTO duelBattleDTO, Player firstPlayer, Player secondPlayer) {
        if(checkPoints(duelBattleDTO.getFirstPlayer().getPoints(),duelBattleDTO.getSecondPlayer().getPoints()))
            throw new NotValidPointsNumber();
        Battle battleWithTableNumber = this.findBattleByTableNumber(duelBattleDTO.getTableNumber());
        battleWithTableNumber.setPoints(firstPlayer,duelBattleDTO.getFirstPlayer().getPoints(),secondPlayer,duelBattleDTO.getSecondPlayer().getPoints());
    }

    public void setPoints(GroupBattleRequestDTO groupBattleDTO, List<Player> firstPlayersGroup, List<Player> secondPlayersGroup) {
        if(checkPoints(groupBattleDTO.getFirstPlayersGroup().getPlayersPoints(),
                groupBattleDTO.getSecondPlayersGroup().getPlayersPoints()))
            throw new NotValidPointsNumber();

        Battle battleWithTableNumber = this.findBattleByTableNumber(groupBattleDTO.getTableNumber());
        battleWithTableNumber.setPoints(firstPlayersGroup,groupBattleDTO.getFirstPlayersGroup().getPlayersPoints(),
                                        secondPlayersGroup,groupBattleDTO.getSecondPlayersGroup().getPlayersPoints());
    }

    public Battle findBattleByTableNumber(int tableNumber){
        return  this.battles.stream()
                .filter(battle -> battle.getTableNumber()==tableNumber)
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Battle.class, String.valueOf(tableNumber)));
    }

    public void checkIfAllBattlesAreFinished() {
        List<Battle> notFinishedBattles = new ArrayList<>();
        battles.forEach(
                battle -> {
                    if(!battle.isFinished())
                        notFinishedBattles.add(battle);
                }
        );
        if(notFinishedBattles.size()==0){
            return;
        }
        if(notFinishedBattles.size()==1){
            if(this.checkIfPlayerIsAloneInBattle()){
                notFinishedBattles.get(0).addAlonePlayer(((DuelTournament)tournament).getPlayersWithoutBattleInTour(number).get(0));
                return;
            }
        }
        throw new BattleOnTableNotFinishedYet(this.number,notFinishedBattles.get(0).getTableNumber());
    }

    public void checkIfAllGroupBattlesAreFinished(){
        List<Battle> notFinishedBattles = new ArrayList<>();
        battles.forEach(
                battle -> {
                    if(!battle.isFinished())
                        notFinishedBattles.add(battle);
                }
        );
        if(notFinishedBattles.size()==0){
            return;
        }
        if(notFinishedBattles.size()==1){
            if(this.checkIfPlayerIsAloneInBattle()){
                notFinishedBattles.get(0).addAlonePlayer(((GroupTournament)tournament).getPlayersWithoutBattleInTour(number).get(0));
                return;
            }
        }
        throw new BattleOnTableNotFinishedYet(this.number,notFinishedBattles.get(0).getTableNumber());
    }

    private boolean checkIfPlayerIsAloneInBattle(){
        return this.tournament.getParticipation().size()%2!=0;
    }

    private boolean checkIfPlayersAreAloneInBattle(){
        return this.tournament.getParticipation().size()/2%2!=0;
    }


    public void setPlayersOnTable(int tableNumber, Player firstPlayer, Player secondPlayer) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    public void setGroupOfPlayersOnTable(int tableNumber, List<Player> firstPlayersGroup, List<Player> secondPlayersGroup) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.addPlayers(firstPlayersGroup,secondPlayersGroup);
    }

    private boolean checkPoints(int firstPointsGroup,int secondPointsGroup){

            if(firstPointsGroup+secondPointsGroup>20 || firstPointsGroup<0 || secondPointsGroup<0)
                return true;

        return false;
    }
}
