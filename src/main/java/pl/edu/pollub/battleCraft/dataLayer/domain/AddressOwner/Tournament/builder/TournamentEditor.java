package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.Date;

@Component
public class TournamentEditor{
    private final TournamentBuilder tournamentBuilder;

    @Autowired
    public TournamentEditor(TournamentBuilder tournamentBuilder) {
        this.tournamentBuilder = tournamentBuilder;
    }

    public TournamentEditor editOrganizedTournament(Tournament tournamentToEdit, String name, int tablesCount, int playersOnTableCount, int toursCount){
        tournamentBuilder.setInstance(tournamentToEdit);
        tournamentBuilder.editBasicData(name,tablesCount,playersOnTableCount,toursCount);
        return this;
    }

    public TournamentEditor editOrganizers(Organizer... coOrganisers) {
        tournamentBuilder.getInstance().editOrganizers(coOrganisers);
        return this;
    }

    public TournamentEditor changeAddress(String province, String city, String street, String zipCode, String description) {
        tournamentBuilder.getInstance().changeAddress(province, city, street, zipCode, description);
        return this;
    }

    public TournamentEditor withGame(Game game){
        tournamentBuilder.getInstance().chooseGame(game);
        return this;
    }

    public TournamentEditor editParticipants(Player... participants) {
        tournamentBuilder.getInstance().editParticipants(participants);
        return this;
    }

    public TournamentEditor startAt(Date startDate){
        tournamentBuilder.getInstance().setDateOfStart(startDate);
        return this;
    }

    public TournamentEditor endingIn(Date endDate){
        tournamentBuilder.getInstance().setDateOfEnd(endDate);
        return this;
    }

    public Tournament finishEditing(){
        return tournamentBuilder.getInstance();
    }
}
