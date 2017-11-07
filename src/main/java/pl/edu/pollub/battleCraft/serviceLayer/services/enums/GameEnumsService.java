package pl.edu.pollub.battleCraft.serviceLayer.services.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;

import java.util.List;

@Service
public class GameEnumsService {

    private final GameRepository gameRepository;

    @Autowired
    public GameEnumsService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<String> getAllAcceptedGamesNames() {
        return gameRepository.getAllAcceptedGamesNames();
    }
}
