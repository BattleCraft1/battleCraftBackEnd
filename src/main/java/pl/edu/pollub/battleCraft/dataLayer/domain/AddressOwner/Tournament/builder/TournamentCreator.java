package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.Date;

@Component
public class TournamentCreator{
    private final TournamentBuilder tournamentBuilder;

    @Autowired
    public TournamentCreator(TournamentBuilder tournamentBuilder) {
        this.tournamentBuilder = tournamentBuilder;
    }

    public TournamentCreator startOrganizeTournament(String name, int tablesCount, int playersOnTableCount, int toursCount){
        tournamentBuilder.setInstance(new Tournament());
        tournamentBuilder.editBasicData(name,tablesCount,playersOnTableCount,toursCount);
        return this;
    }

    public TournamentCreator with(Organizer... coOrganisers){
        tournamentBuilder.getInstance().addOrganizers(coOrganisers);
        return this;
    }

    public TournamentCreator in(Address address){
        tournamentBuilder.getInstance().initAddress(address);
        return this;
    }

    public TournamentCreator withGame(Game game){
        tournamentBuilder.getInstance().chooseGame(game);
        return this;
    }

    public TournamentCreator inviteParticipants(Player... participants){
        tournamentBuilder.getInstance().addParticipants(participants);
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
