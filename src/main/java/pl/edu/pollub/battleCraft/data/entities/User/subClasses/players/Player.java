package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Player extends UserAccount {

    public Player() {
        super(UserType.ACCEPTED);
        this.banned = false;
    }

    public Player(UserType userType) {
        super(userType);
        this.banned = false;
    }

    public Player(UserAccount userAccount) {
        this(UserType.ACCEPTED);
        this.setFirstname(userAccount.getFirstname());
        this.setLastname(userAccount.getLastname());
        this.setName(userAccount.getName());
        this.setEmail(userAccount.getEmail());
        this.setPassword(userAccount.getPassword());
        this.setPhoneNumber(userAccount.getPhoneNumber());
        this.changeAddress(userAccount.getAddress().clone());
    }

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "player")
    private List<Participation> participatedTournaments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "player")
    private List<Play> battles = new ArrayList<>();

    private boolean banned;

    public void addParticipation(Participation participation) {
        this.deleteParticipationWithTheSameTournamentName(participation.getTournament().getName());
        this.participatedTournaments.add(participation);
    }

    private void deleteParticipationWithTheSameTournamentName(String tournamentName){
        Participation participation = this.participatedTournaments.stream()
                .filter(participation1 -> participation1.getTournament().getName().equals(tournamentName))
                .findFirst().orElse(null);
        if(participation!=null){
            this.participatedTournaments.remove(participation);
        }
    }

    protected void setParticipatedTournaments(List<Participation> participatedTournaments){
        this.participatedTournaments = participatedTournaments;
    }

    public void addBattles(Map<Player,Battle> battleMap){
        battleMap.forEach(
                (player, battle) -> {
                    Play firstPlayerPlay = new Play(this,battle);
                    Play secondPlayerPlay = new Play(player,battle);
                    this.battles.addAll(Arrays.asList(firstPlayerPlay,secondPlayerPlay));
                    battle.addPlayersByOneSide(firstPlayerPlay,secondPlayerPlay);
                }
        );
    }

    public void addBattlesByOneSide(Play battle){
        this.battles.add(battle);
    }
}
