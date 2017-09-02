package pl.edu.pollub.battleCraft.data.entities.Tournament;

import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class TournamentBuilder {
    private Tournament instance;

    public TournamentBuilder(){

    }

    public TournamentBuilder createTournament(String name, int maxPlayers, int tablesCount,
                                              TournamentClass tournamentClass, Game game){
        instance = new Tournament();
        instance.setName(name);
        instance.setMaxPlayers(maxPlayers);
        instance.setTablesCount(tablesCount);
        instance.setTournamentClass(tournamentClass);
        instance.setGame(game);
        return this;
    }

    public TournamentBuilder startAt(Date startDate){
        instance.setDateOfStart(startDate);
        return this;
    }

    public TournamentBuilder endingIn(Date endDate){
        instance.setDateOfEnd(endDate);
        return this;
    }

    public TournamentBuilder organizedBy(Organizer... organizers){
        this.instance.setOrganizers(Arrays.stream(organizers)
                .map(organizer -> new Organization(organizer,instance))
                .collect(Collectors.toList()));
        return this;
    }

    public TournamentBuilder withParticipants(Player... participants){
        this.instance.setParticipants(
                Arrays.stream(participants).map(participant -> new Participation(participant,instance)
                ).collect(Collectors.toList()));
        return this;
    }
}
