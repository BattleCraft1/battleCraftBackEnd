package pl.edu.pollub.battleCraft.serviceLayer.services.resources.implementations;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.FileService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;

@Service
public class GameResourcesServiceImpl implements GameResourcesService{

    private final String DEFAULT_GAME_RULES_DIRECTORY_NAME = "gameRules";

    private final FileService fileService;

    public GameResourcesServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Resource getGameRules(String gameName) {
        return fileService.loadAsResource(new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME)
                .append("/").append(gameName).append(".pdf").toString());
    }

    @Override
    public void deleteGamesRules(String... gamesToDeleteUniqueNames) {
        fileService.deleteFilesReletedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME,gamesToDeleteUniqueNames);
    }
}
