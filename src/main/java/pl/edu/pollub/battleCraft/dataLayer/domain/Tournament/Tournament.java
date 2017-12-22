package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwnership.AddressOwnership;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.Play.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Organization.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Tournament{

    protected Tournament(int playersOnTableCount) {
        this.playersOnTableCount = playersOnTableCount;
        this.status = TournamentStatus.NEW;
        this.banned = false;
        this.addressOwnership = new AddressOwnership();
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column(length = 30, unique = true)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfEnd;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    private boolean banned;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "participatedTournament")
    protected List<Participation> participation = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "organizedTournament")
    private List<Organization> organizations = new ArrayList<>();

    private int tablesCount;

    private int playersOnTableCount;

    private int turnsCount;

    @Formula("tables_count * players_on_table_count")
    private int maxPlayers;

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("(select count(*) from organization o where o.tournament_id = id)")
    private int organizersNumber;

    @Formula("(tables_count * players_on_table_count)-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Turn> turns = new ArrayList<>();

    private int currentTurnNumber;

    @Embedded
    private AddressOwnership addressOwnership;

    public int getTableNumberForPlayer(Player firstPlayer, int turnNumber) {
        return this.getTurnByNumber(turnNumber).getBattles().stream()
                .filter(battle -> battle.getPlayers().stream()
                        .filter(play -> play.getPlayer().equals(firstPlayer))
                        .findFirst().orElse(null)!=null)
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Battle.class,firstPlayer.getName()))
                .getTableNumber();
    }

    public Player getPlayerByName(String playerName){
        return this.getParticipation().stream()
                .map(Participation::getPlayer)
                .filter(player -> player.getName().equals(playerName))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class,playerName));
    }

    public Participation getParticipationByPlayerName(String playerName){
        return this.getParticipation().stream()
                .filter(participation -> participation.getPlayer().getName().equals(playerName))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class,playerName));
    }

    public Turn getTurnByNumber(int number){
        return this.getTurns().stream().filter(tour -> tour.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(Turn.class,String.valueOf(currentTurnNumber)));
    }

    public int getPointsForPlayer(Player player){
        return this.getTurns().subList(0,this.getCurrentTurnNumber()+1).stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
                .mapToInt(Play::getPoints).sum();
    }

    public List<Turn> getActivatedTurns(){
        return this.getTurns().subList(0, currentTurnNumber +1);
    }

    protected List<Turn> getPreviousTurns(){
        return this.getTurns().subList(0, currentTurnNumber);
    }

    public void filterNoAcceptedOrganizations(){
        this.organizations.removeAll(this.organizations.stream()
                .filter(organization -> !organization.isAccepted())
                .peek(organization -> {
                    organization.getOrganizer().deleteOrganizationByOneSide(organization);
                    organization.setOrganizer(null);
                    organization.setOrganizedTournament(null);
                }).collect(Collectors.toList()));
    }

    public void addOrganizationByOneSide(Organization organization) {
        this.deleteOrganizationWithTheSameTournamentName(organization.getOrganizedTournament().getName());
        this.organizations.add(organization);
    }

    public void deleteOrganizationByOneSide(Organization organization){
        if(this.organizations.contains(organization))
            this.organizations.remove(organization);
    }

    private void deleteOrganizationWithTheSameTournamentName(String organizerName){
        Organization organization = this.organizations.stream()
                .filter(organization1 -> organization1.getOrganizer().getName().equals(organizerName))
                .findFirst().orElse(null);
        if(organization!=null){
            this.organizations.remove(organization);
        }
    }

    public void addParticipationByOneSide(Participation participation) {
        this.deleteParticipationWithTheSameTournamentName(participation.getParticipatedTournament().getName());
        this.participation.add(participation);
    }

    public void deleteParticipationByOneSide(Participation participation){
        if(this.participation.contains(participation))
            this.participation.remove(participation);
    }

    private void deleteParticipationWithTheSameTournamentName(String playerName){
        Participation participation = this.participation.stream()
                .filter(participation1 -> participation1.getPlayer().getName().equals(playerName))
                .findFirst().orElse(null);
        if(participation!=null){
            this.participation.remove(participation);
        }
    }

    public void chooseGame(Game game){
        this.game = game;
        game.addTournamentByOneSide(this);
    }

    public TournamentType getTournamentType(){
        if(this.playersOnTableCount==2){
            return TournamentType.DUEL;
        }
        return TournamentType.GROUP;
    }

    protected void setGame(Game game){
        this.game = game;
    }

    private void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    private void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    private void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    private void setParticipation(List<Participation> participation){
        this.participation = participation;
    }

    private void setOrganizations(List<Organization> organizations){
        this.organizations = organizations;
    }
}
