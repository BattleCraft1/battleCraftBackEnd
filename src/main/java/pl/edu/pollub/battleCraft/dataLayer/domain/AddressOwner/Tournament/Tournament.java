package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    private boolean banned;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "participatedTournament")
    protected Set<Participation> participation = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "organizedTournament")
    private Set<Organization> organizations = new HashSet<>();

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
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class,playerName));
    }

    public Participation getParticipationByPlayerName(String playerName){
        return this.getParticipation().stream()
                .filter(participation -> participation.getPlayer().getName().equals(playerName))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class,playerName));
    }

    public Tour getTourByNumber(int number){
        return this.getTours().stream().filter(tour -> tour.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(Tour.class,String.valueOf(currentTourNumber)));
    }

    public int getPointsForPlayer(Player player){
        return this.getTours().subList(0,this.getCurrentTourNumber()+1).stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
                .mapToInt(Play::getPoints).sum();
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

    public TournamentType getTournamentType(){
        if(this.playersOnTableCount==2){
            return TournamentType.DUEL;
        }
        return TournamentType.GROUP;
    }

    public int getTableNumberForPlayer(Player firstPlayer, int tourNumber) {
        return this.getTourByNumber(tourNumber).getBattles().stream()
                .filter(battle -> battle.getPlayers().stream()
                        .filter(play -> play.getPlayer().equals(firstPlayer))
                        .findFirst().orElse(null)!=null)
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Battle.class,firstPlayer.getName()))
                .getTableNumber();
    }

    protected void setGame(Game game){
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tournament)) return false;
        if (!super.equals(o)) return false;

        Tournament that = (Tournament) o;

        if (isBanned() != that.isBanned()) return false;
        if (getTablesCount() != that.getTablesCount()) return false;
        if (getPlayersOnTableCount() != that.getPlayersOnTableCount()) return false;
        if (getToursCount() != that.getToursCount()) return false;
        if (getMaxPlayers() != that.getMaxPlayers()) return false;
        if (getPlayersNumber() != that.getPlayersNumber()) return false;
        if (getOrganizersNumber() != that.getOrganizersNumber()) return false;
        if (getFreeSlots() != that.getFreeSlots()) return false;
        if (getCurrentTourNumber() != that.getCurrentTourNumber()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDateOfStart() != null ? !getDateOfStart().equals(that.getDateOfStart()) : that.getDateOfStart() != null)
            return false;
        if (getDateOfEnd() != null ? !getDateOfEnd().equals(that.getDateOfEnd()) : that.getDateOfEnd() != null)
            return false;
        if (getGame() != null ? getGame().getName() != null ? !getGame().getName().equals(that.getGame().getName()) : getGame().getName() != null : that.getGame() != null) return false;
        if (getStatus() != that.getStatus()) return false;
        if (getParticipation() != null ? !getParticipation().equals(that.getParticipation()) : that.getParticipation() != null)
            return false;
        if (getOrganizations() != null ? !getOrganizations().equals(that.getOrganizations()) : that.getOrganizations() != null)
            return false;
        return getTours() != null ? getTours().equals(that.getTours()) : that.getTours() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDateOfStart() != null ? getDateOfStart().hashCode() : 0);
        result = 31 * result + (getDateOfEnd() != null ? getDateOfEnd().hashCode() : 0);
        result = 31 * result + (getGame() != null ? getGame().getName() != null ? getGame().getName().hashCode() : 0 : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (isBanned() ? 1 : 0);
        result = 31 * result + (getParticipation() != null ? getParticipation().hashCode() : 0);
        result = 31 * result + (getOrganizations() != null ? getOrganizations().hashCode() : 0);
        result = 31 * result + getTablesCount();
        result = 31 * result + getPlayersOnTableCount();
        result = 31 * result + getToursCount();
        result = 31 * result + getMaxPlayers();
        result = 31 * result + getPlayersNumber();
        result = 31 * result + getOrganizersNumber();
        result = 31 * result + getFreeSlots();
        result = 31 * result + (getTours() != null ? getTours().hashCode() : 0);
        result = 31 * result + getCurrentTourNumber();
        return result;
    }
}
