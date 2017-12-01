package pl.edu.pollub.battleCraft.serviceLayer.services.resources;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.GameRules.InvalidGameRulesExtension;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;
import pl.edu.pollub.battleCraft.serviceLayer.services.file.FileService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
public class GameResourcesService {

    private final String DEFAULT_GAME_RULES_DIRECTORY_NAME = "gameRules";

    private final FileService fileService;

    private final GameRepository gameRepository;
    private final AuthorityRecognizer authorityRecognizer;

    public GameResourcesService(FileService fileService, GameRepository gameRepository, AuthorityRecognizer authorityRecognizer) {
        this.fileService = fileService;
        this.gameRepository = gameRepository;
        this.authorityRecognizer = authorityRecognizer;
    }

    public Resource getGameRules(String gameName) {
        return fileService.loadAsResource(new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME)
                .append("/").append(gameName).append(".pdf").toString());
    }

    public void deleteGamesRules(String... gamesToDeleteUniqueNames) {
        fileService.deleteFilesRelatedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME, Arrays.asList(gamesToDeleteUniqueNames));
    }

    public void renameGamesRules(String previousName,String newName) {
        fileService.renameRelatedWithEntityFile(DEFAULT_GAME_RULES_DIRECTORY_NAME,previousName,newName);
    }

    public void saveGameRules(@NotNull @NotBlank String gameName, @NotNull @NotBlank MultipartFile file){
        Game game = Optional.ofNullable(gameRepository.checkIfGameExist(gameName))
                .orElseThrow(() -> new ObjectNotFoundException(Game.class,gameName));

        authorityRecognizer.checkIfCurrentUserIsCreatorOfGame(game);

        fileService.deleteFilesRelatedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME, Collections.singletonList(game.getName()));

        String extension = file.getOriginalFilename().split("\\.")[1];

        if(!extension.equals("pdf"))
            throw new InvalidGameRulesExtension(extension);

        fileService.store(file,new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME).append("/").append(game.getName()).toString(),extension);
    }

}
