package pl.edu.pollub.battleCraft.dataLayer.repositories.enums.implementations;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.repositories.enums.interfaces.GamesEnumsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;

import java.util.List;

@Component
public class GamesEnumsRepositoryImpl implements GamesEnumsRepository{
    private final GameRepository gameRepository;

    public GamesEnumsRepositoryImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }
}
