package pl.edu.pollub.battleCraft.serviceLayer.services.enums.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.dataLayer.repositories.enums.implementations.GamesEnumsRepositoryImpl;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;

import java.util.List;

@Service
public class GameEnumsServiceImpl implements GameEnumsService{

    private final GamesEnumsRepositoryImpl gamesEnumsRepository;

    @Autowired
    public GameEnumsServiceImpl(GamesEnumsRepositoryImpl gamesEnumsRepository) {
        this.gamesEnumsRepository = gamesEnumsRepository;
    }

    @Override
    public List<String> getAllGamesStatus() {
        return GameStatus.getNames();
    }

    @Override
    public List<String> getAllGamesNames() {
        return gamesEnumsRepository.getAllGamesNames();
    }
}
