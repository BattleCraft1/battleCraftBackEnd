package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentCreator{
    private final TournamentBuilder tournamentBuilder;

    @Autowired
    public TournamentCreator(TournamentBuilder tournamentBuilder) {
        this.tournamentBuilder = tournamentBuilder;
    }

    public TournamentCreator startOrganizeTournament(String name, int tablesCount, TournamentType tournamentType, int toursCount){
        if(tournamentType == TournamentType.DUEL)
            tournamentBuilder.setInstance(new DuelTournament());
        else
            tournamentBuilder.setInstance(new GroupTournament());

        tournamentBuilder.editBasicData(name,tablesCount,tournamentType.value(),toursCount);
        return this;
    }

    public TournamentCreator in(Address address){
        tournamentBuilder.getInstance().getAddressOwnership().insertAddress(address);
        return this;
    }

    public TournamentCreator withGame(Game game){
        tournamentBuilder.getInstance().chooseGame(game);
        return this;
    }

    public TournamentCreator startAt(Date startDate){
        tournamentBuilder.getInstance().setDateOfStart(startDate);
        return this;
    }

    public TournamentCreator endingIn(Date endDate){
        tournamentBuilder.getInstance().setDateOfEnd(endDate);
        return this;
    }

    public Tournament finishOrganize(){
        return tournamentBuilder.getInstance();
    }
}
