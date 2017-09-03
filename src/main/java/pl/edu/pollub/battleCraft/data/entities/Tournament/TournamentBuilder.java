package pl.edu.pollub.battleCraft.data.entities.Tournament;

import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import java.util.Date;

public class TournamentBuilder {
    private Tournament instance;

    public TournamentBuilder() {

    }

    public TournamentBuilder create(String name, int maxPlayers, int tablesCount,
                                    TournamentClass tournamentClass) {
        instance = new Tournament();
        instance.setName(name);
        instance.setMaxPlayers(maxPlayers);
        instance.setTablesCount(tablesCount);
        instance.setTournamentClass(tournamentClass);
        return this;
    }

    public TournamentBuilder in(Address address){
        instance.setAddress(address);
        return this;
    }

    public TournamentBuilder withGame(Game game){
        instance.setGame(game);
        return this;
    }

    public TournamentBuilder startAt(Date startDate) {
        instance.setDateOfStart(startDate);
        return this;
    }

    public TournamentBuilder endingIn(Date endDate) {
        instance.setDateOfEnd(endDate);
        return this;
    }

    public TournamentBuilder organizedBy(Organizer... organizers) {
        this.instance.setOrganizers(organizers);
        return this;
    }

    public TournamentBuilder withParticipants(Player... participants) {
        this.instance.setParticipants(participants);
        return this;
    }

    public Tournament build(){
        return instance;
    }
}
