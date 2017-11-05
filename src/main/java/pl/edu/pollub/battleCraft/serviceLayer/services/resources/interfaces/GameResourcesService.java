package pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface GameResourcesService {
    Resource getGameRules(String gameName);

    void deleteGamesRules(String... gamesToDeleteUniqueNames);

    void renameGamesRules(String previousName, String newName);

    void saveGameRules(String gameName, MultipartFile file);
}
