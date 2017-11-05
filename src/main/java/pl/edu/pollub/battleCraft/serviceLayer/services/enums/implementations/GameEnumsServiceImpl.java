package pl.edu.pollub.battleCraft.serviceLayer.services.enums.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;

import java.util.List;

@Service
public class GameEnumsServiceImpl implements GameEnumsService{

    private final GameRepository gameRepository;

    @Autowired
    public GameEnumsServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<String> getAllAcceptedGamesNames() {
        return gameRepository.getAllAcceptedGamesNames();
    }
}
