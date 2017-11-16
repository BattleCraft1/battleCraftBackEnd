package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Tournament extends AddressOwner{
    protected Tournament(int playersOnTableCount) {
        this.playersOnTableCount = playersOnTableCount;
        this.status = TournamentStatus.NEW;
        this.banned = false;
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

    private int toursCount;

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
    private List<Tour> tours = new ArrayList<>();

    private int currentTourNumber;

    public Player getPlayerByName(String playerName){
        return this.getParticipation().stream()
                .map(Participation::getPlayer)
                .filter(player -> player.getName().equals(playerName))
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Player.class,playerName));
    }

    public Participation getParticipationByPlayerName(String playerName){
        return this.getParticipation().stream()
                .filter(participation -> participation.getPlayer().getName().equals(playerName))
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Player.class,playerName));
    }

    public Tour getTourByNumber(int number){
        return this.getTours().stream().filter(tour -> tour.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Tour.class,new StringBuilder(currentTourNumber).toString()));
    }

    public List<Tour> getActivatedTours(){
        return this.getTours().subList(0,currentTourNumber+1);
    }

    protected List<Tour> getPreviousTours(){
        return this.getTours().subList(0,currentTourNumber);
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

    public void addOrganizers(List<Organizer> organizers){
        this.organizations.addAll(organizers.stream()
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void editOrganizers(List<Organizer> organizers) {
        this.addNewOrganizations(organizers);
        this.removeNotExistingOrganizations(organizers);

    }

    private void addNewOrganizations(List<Organizer> organizers){
        this.organizations.addAll(organizers.stream()
                .filter(organizer -> !this.organizations.stream()
                        .map(Organization::getOrganizer)
                        .collect(Collectors.toList()).contains(organizer))
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganizationByOneSide(organization);
                    return organization; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingOrganizations(List<Organizer> organizers){
        this.organizations.removeAll(this.organizations.stream()
                .filter(organization -> !organizers.contains(organization.getOrganizer()))
                .peek(organization -> {
                    organization.getOrganizer().deleteOrganizationByOneSide(organization);
                    organization.setOrganizedTournament(null);
                    organization.setOrganizer(null);
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

    public long getNoExistingGroupNumber(){
        List<Long> groupsNumbers = this.participation.stream()
                .map(Participation::getGroupNumber)
                .distinct()
                .collect(Collectors.toList());
        long possibleGroupNumber = new Random().nextLong();
        while (groupsNumbers.contains(possibleGroupNumber)){
            possibleGroupNumber = new Random().nextLong();
        }
        return possibleGroupNumber;
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

    public int getTableNumberForPlayer(Player firstPlayer) {
        return this.getTourByNumber(this.getCurrentTourNumber()).getBattles().stream()
                .filter(battle -> battle.getPlayers().stream()
                        .filter(play -> play.getPlayer().equals(firstPlayer))
                        .findFirst().orElse(null)!=null)
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Battle.class,firstPlayer.getName()))
                .getTableNumber();
    }
}
