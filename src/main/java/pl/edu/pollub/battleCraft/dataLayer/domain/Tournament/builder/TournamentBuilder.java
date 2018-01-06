package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;

@Component
public class TournamentBuilder {

    private Tournament instance;

    Tournament getInstance() {
        return instance;
    }

    void setInstance(Tournament instance) {
        this.instance = instance;
    }

    void editBasicData(String name, int tablesCount, int playersOnTableCount, int toursCount){
        instance.setName(name);
        instance.setTablesCount(tablesCount);
        instance.setPlayersOnTableCount(playersOnTableCount);
        instance.setTurnsCount(toursCount);
    }
}
