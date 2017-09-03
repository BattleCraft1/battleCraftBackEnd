package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.service.services.interfaces.GameService;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }
}
