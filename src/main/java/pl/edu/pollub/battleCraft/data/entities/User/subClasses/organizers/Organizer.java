package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.Tournament.subClasses.tournamentWithProgression.TournamentWithProgression;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization.GameNotAcceptedException;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization.NotPossibleEndDate;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization.OutdatedStartDate;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization.TimeOfTournamentIsLong;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.YouDidNotOrganizedTournamentWithThisName;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Transient
    private Tournament tournamentInOrganisation;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "organizer")
    private List<Organization> organizedTournaments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<Game> createdGames = new ArrayList<>();

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

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

    private void chooseParticipatedTournaments(List<Participation> participatedTournaments) {
        participatedTournaments.forEach(participation -> participation.setPlayer(this));
        this.setParticipatedTournaments(participatedTournaments);
    }

    @Transient
    public Game createGame(String name){
        Game createdGame = new Game(name,this);
        createdGames.add(createdGame);

        return createdGame;
    }

    @JsonIgnore
    @Transient
    public Organizer startOrganizeTournament(String name, int tablesCount, int maxPlayers){
        tournamentInOrganisation = new Tournament();
        tournamentInOrganisation.setName(name);
        tournamentInOrganisation.setTablesCount(tablesCount);
        tournamentInOrganisation.initMaxPlayers(maxPlayers);
        tournamentInOrganisation.addOrganizers(this);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer with(Organizer... coOrganisers){
        tournamentInOrganisation.addOrganizers(coOrganisers);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer in(Address address){
        tournamentInOrganisation.changeAddress(address);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer withGame(Game game){
        if(game.getStatus()!= GameStatus.ACCEPTED)
            throw new GameNotAcceptedException(game.getName());
        tournamentInOrganisation.chooseGame(game);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer startAt(Date startDate) {
        if(startDate.before(new Date())){

            throw new OutdatedStartDate(format.format(startDate));
        }
        tournamentInOrganisation.setDateOfStart(startDate);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer endingIn(Date endDate) {
        Date startDate = tournamentInOrganisation.getDateOfStart();
        if(endDate.before(startDate)){
            throw new NotPossibleEndDate(format.format(startDate));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE,3);
        Date maxEndDate = calendar.getTime();
        if(endDate.after(maxEndDate)){
            throw new TimeOfTournamentIsLong();
        }
        tournamentInOrganisation.setDateOfEnd(endDate);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer inviteParticipants(Player... participants) {
        tournamentInOrganisation.addParticipants(participants);
        return this;
    }

    @JsonIgnore
    @Transient
    public Tournament finishOrganize(){
        return tournamentInOrganisation;
    }

    @JsonIgnore
    @Transient
    public TournamentWithProgression startTournament(String tournamentToStartName,int toursNumber){
        Tournament tournamentToStart = this.findOrganizedTournamentByName(tournamentToStartName);
        return new TournamentWithProgression(tournamentToStart,toursNumber);
    }

    public void addOrganization(Organization organization) {
        this.organizedTournaments.add(organization);
    }

    private void setOrganizedTournaments(List<Organization> organizedTournaments){
        this.organizedTournaments = organizedTournaments;
    }

    private void setCreatedGames(List<Game> createdGames){
        this.createdGames = createdGames;
    }

    private Tournament findOrganizedTournamentByName(String tournamentName){
        return   organizedTournaments.stream()
                .map(Organization::getTournament)
                .filter(tournament -> tournament.getName().equals(tournamentName))
                .findFirst().orElseThrow(() -> new YouDidNotOrganizedTournamentWithThisName(tournamentName));
    }
}
