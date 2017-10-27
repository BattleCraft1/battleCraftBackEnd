package pl.edu.pollub.battleCraft.serviceLayer.services.resources.implementations;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.GameRules.InvalidGameRulesExtension;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.FileService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class GameResourcesServiceImpl implements GameResourcesService{

    private final String DEFAULT_GAME_RULES_DIRECTORY_NAME = "gameRules";

    private final FileService fileService;

    private final GameRepository gameRepository;

    public GameResourcesServiceImpl(FileService fileService, GameRepository gameRepository) {
        this.fileService = fileService;
        this.gameRepository = gameRepository;
    }

    @Override
    public Resource getGameRules(String gameName) {
        return fileService.loadAsResource(new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME)
                .append("/").append(gameName).append(".pdf").toString());
    }

    @Override
    public void deleteGamesRules(String... gamesToDeleteUniqueNames) {
        fileService.deleteFilesRelatedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME,gamesToDeleteUniqueNames);
    }


    @Override
    public void saveGameRules(@NotNull @NotBlank String gameName, @NotNull @NotBlank MultipartFile file){
        String name = Optional.ofNullable(gameRepository.checkIfGameExist(gameName))
                .orElseThrow(() -> new EntityNotFoundException(Game.class,gameName));

        fileService.deleteFilesRelatedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME,name);

        String extension = file.getOriginalFilename().split("\\.")[1];

        if(!extension.equals("pdf"))
            throw new InvalidGameRulesExtension(extension);

        fileService.store(file,new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME).append("/").append(name).toString(),extension);
    }
}
