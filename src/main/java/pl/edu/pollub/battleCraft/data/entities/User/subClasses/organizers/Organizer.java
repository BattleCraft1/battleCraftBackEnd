package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public Organizer(Player player) {
        this();
        this.setId(player.getId());
        this.setFirstname(player.getFirstname());
        this.setLastname(player.getLastname());
        this.setName(player.getName());
        this.setEmail(player.getEmail());
        this.setPassword(player.getPassword());
        this.setPhoneNumber(player.getPhoneNumber());
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
        tournamentInOrganisation.chooseGame(game);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer startAt(Date startDate) {
        tournamentInOrganisation.setDateOfStart(startDate);
        return this;
    }

    @JsonIgnore
    @Transient
    public Organizer endingIn(Date endDate) {
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

    public void addOrganization(Organization organization) {
        this.organizedTournaments.add(organization);
    }

    private void setOrganizedTournaments(List<Organization> organizedTournaments){
        this.organizedTournaments = organizedTournaments;
    }

    private void setCreatedGames(List<Game> createdGames){
        this.createdGames = createdGames;
    }
}
