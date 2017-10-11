package pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces;

import org.springframework.core.io.Resource;

public interface GameResourcesService {
    Resource getGameRules(String gameName);

    void deleteGamesRules(String... gamesToDeleteUniqueNames);
}
