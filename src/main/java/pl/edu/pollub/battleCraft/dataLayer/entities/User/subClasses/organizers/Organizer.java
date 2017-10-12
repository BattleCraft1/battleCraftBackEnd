package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.subClasses.tournamentWithProgression.TournamentWithProgression;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.start.ThisTournamentIsNotStarted;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.start.YouDidNotOrganizeTournamentWithThisName;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Organizer extends Player {
    public Organizer() {
        super(UserType.ORGANIZER);
    }

    public Organizer(Player player){
        this();
        this.setFirstname(player.getFirstname());
        this.setLastname(player.getLastname());
        this.setName(player.getName());
        this.setEmail(player.getEmail());
        this.setPassword(player.getPassword());
        this.setPhoneNumber(player.getPhoneNumber());
        this.chooseParticipatedTournaments(
                player.getParticipatedTournaments().stream().map(Participation::clone).collect(Collectors.toList()));
        this.changeAddress(player.getAddress().clone());
    }

    @Transient
    private Tournament tournamentInOrganisation;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "organizer")
    private List<Organization> organizedTournaments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<Game> createdGames = new ArrayList<>();

    private void chooseParticipatedTournaments(List<Participation> participatedTournaments) {
        participatedTournaments.forEach(participation -> participation.setPlayer(this));
        this.setParticipatedTournaments(participatedTournaments);
    }

    public Game createGame(String name){
        Game createdGame = new Game(name,this);
        createdGames.add(createdGame);

        return createdGame;
    }

    @JsonIgnore
    public Organizer startOrganizeTournament(String name, int tablesCount, int maxPlayers){
        tournamentInOrganisation = new Tournament();
        return editBasicData(name,tablesCount,maxPlayers);
    }

    @JsonIgnore
    public Organizer editOrganizedTournament(Tournament tournament, String name, int tablesCount, int maxPlayers){
        tournamentInOrganisation = tournament;
        return editBasicData(name,tablesCount,maxPlayers);
    }

    @JsonIgnore
    private Organizer editBasicData(String name, int tablesCount, int maxPlayers){
        tournamentInOrganisation.setName(name);
        tournamentInOrganisation.setTablesCount(tablesCount);
        tournamentInOrganisation.initMaxPlayers(maxPlayers);
        tournamentInOrganisation.addOrganizers(this);
        return this;
    }

    @JsonIgnore
    public Organizer with(Organizer... coOrganisers){
        tournamentInOrganisation.addOrganizers(coOrganisers);
        return this;
    }

    @JsonIgnore
    public Organizer in(Address address){
        tournamentInOrganisation.changeAddress(address);
        return this;
    }

    @JsonIgnore
    public Organizer withGame(Game game){
        tournamentInOrganisation.chooseGame(game);
        return this;
    }

    @JsonIgnore
    public Organizer startAt(Date startDate){
        tournamentInOrganisation.setDateOfStart(startDate);
        return this;
    }

    @JsonIgnore
    public Organizer endingIn(Date endDate){
        tournamentInOrganisation.setDateOfEnd(endDate);
        return this;
    }

    @JsonIgnore
    public Organizer inviteParticipants(Player... participants){
        tournamentInOrganisation.addParticipants(participants);
        return this;
    }

    @JsonIgnore
    public Tournament finishOrganize(){
        return tournamentInOrganisation;
    }

    public Tournament startTournament(String tournamentToStartName,int toursNumber){
        Tournament tournamentToStart = this.findOrganizedTournamentByName(tournamentToStartName);
        return new TournamentWithProgression(tournamentToStart,toursNumber);
    }

    public void setRandomPlayersOnTableInFirstTour(String tournamentName,int tableNumber){
        TournamentWithProgression tournamentWithProgression = this.findStartedTournamentByName(tournamentName);
        tournamentWithProgression.setRandomPlayersOnTableInFirstTour(tableNumber);
    }

    public void setPlayersOnTableInFirstTour(String tournamentName,int tableNumber, Player firstPlayer, Player secondPlayer){
        TournamentWithProgression tournamentWithProgression = this.findStartedTournamentByName(tournamentName);
        tournamentWithProgression.setPlayersOnTableInFirstTour(tableNumber,firstPlayer,secondPlayer);
    }

    public void setPointsOnTable(String tournamentName, int tableNumber, int pointsForFirstPlayer) {
        TournamentWithProgression tournamentWithProgression = this.findStartedTournamentByName(tournamentName);
        tournamentWithProgression.setPointsOnTable(tableNumber,pointsForFirstPlayer);
    }

    public void prepareNextTour(String tournamentName) {
        TournamentWithProgression tournamentWithProgression = this.findStartedTournamentByName(tournamentName);
        tournamentWithProgression.prepareNextTour();
    }

    public void finishTournament(String tournamentName) {
        TournamentWithProgression tournamentToFinish = this.findStartedTournamentByName(tournamentName);
        tournamentToFinish.finishTournament();
    }

    @JsonIgnore
    private TournamentWithProgression findStartedTournamentByName(String tournamentName){
        return this.castTournamentToTournamentWithProgression(this.findOrganizedTournamentByName(tournamentName));
    }

    @JsonIgnore
    private TournamentWithProgression castTournamentToTournamentWithProgression(Tournament tournament){
        if(tournament instanceof TournamentWithProgression)
            return (TournamentWithProgression) tournament;
        else
            throw new ThisTournamentIsNotStarted(tournament.getName());
    }

    public void addOrganization(Organization organization) {
        this.deleteOrganizationWithTheSameTournamentName(organization.getOrganizedTournament().getName());
        this.organizedTournaments.add(organization);
    }

    public void deleteOrganizationWithTheSameTournamentName(String tournamentName){
        Organization organization = this.organizedTournaments.stream()
                .filter(participation1 -> participation1.getOrganizedTournament().getName().equals(tournamentName))
                .findFirst().orElse(null);
        if(organization!=null){
            this.organizedTournaments.remove(organization);
        }
    }

    private void setOrganizedTournaments(List<Organization> organizedTournaments){
        this.organizedTournaments = organizedTournaments;
    }

    private void setCreatedGames(List<Game> createdGames){
        this.createdGames = createdGames;
    }

    private Tournament findOrganizedTournamentByName(String tournamentName){
        return   organizedTournaments.stream()
                .map(Organization::getOrganizedTournament)
                .filter(tournament -> tournament.getName().equals(tournamentName))
                .findFirst().orElseThrow(() -> new YouDidNotOrganizeTournamentWithThisName(tournamentName));
    }

}
