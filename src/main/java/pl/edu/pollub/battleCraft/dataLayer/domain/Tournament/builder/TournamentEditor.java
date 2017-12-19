package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentEditor{
    private final TournamentBuilder tournamentBuilder;

    @Autowired
    public TournamentEditor(TournamentBuilder tournamentBuilder) {
        this.tournamentBuilder = tournamentBuilder;
    }

    public TournamentEditor editOrganizedTournament(Tournament tournamentToEdit, String name, int tablesCount, TournamentType tournamentType, int toursCount){
        tournamentBuilder.setInstance(tournamentToEdit);
        tournamentBuilder.editBasicData(name,tablesCount,tournamentType.value(),toursCount);
        return this;
    }

    public TournamentEditor editOrganizers(List<Organizer> coOrganisers) {
        tournamentBuilder.getInstance().editOrganizers(coOrganisers);
        return this;
    }

    public TournamentEditor changeAddress(String province, String city, String street, String zipCode, String description) {
        tournamentBuilder.getInstance().getAddressOwnership().changeAddress(province, city, street, zipCode, description);
        return this;
    }

    public TournamentEditor withGame(Game game){
        tournamentBuilder.getInstance().chooseGame(game);
        return this;
    }

    public TournamentEditor editParticipants(List<List<Player>>  participants) {
        if(tournamentBuilder.getInstance().getPlayersOnTableCount() == TournamentType.DUEL.value())
            ((DuelTournament)tournamentBuilder.getInstance()).editParticipants(participants.stream().flatMap(List::stream).collect(Collectors.toList()));
        else
            ((GroupTournament)tournamentBuilder.getInstance()).editParticipants(participants);
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
