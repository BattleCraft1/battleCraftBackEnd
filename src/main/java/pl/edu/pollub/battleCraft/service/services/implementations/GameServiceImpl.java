package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.entities.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedGameRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.service.services.helpers.file.interfaces.FileService;
import pl.edu.pollub.battleCraft.service.services.interfaces.GameService;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private final ExtendedGameRepository gameRepository;

    private final FileService fileService;

    private final String DEFAULT_GAME_RULES_DIRECTORY_NAME = "gameRules";

    @Autowired
    public GameServiceImpl(ExtendedGameRepository gameRepository, FileService fileService) {
        this.gameRepository = gameRepository;
        this.fileService = fileService;
    }

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }

    @Override
    public Page getPageOfGames(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return gameRepository.getPageOfGames(searchCriteria, requestedPage);
    }

    @Override
    public void banGames(String... gamesToBanUniqueNames) {
        gameRepository.banGames(gamesToBanUniqueNames);
    }

    @Override
    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gameRepository.unlockGames(gamesToUnlockUniqueNames);
    }

    @Override
    public void deleteGames(String... gamesToDeleteUniqueNames) {
        gameRepository.deleteGames(gamesToDeleteUniqueNames);
        fileService.deleteFilesReletedWithEntities(DEFAULT_GAME_RULES_DIRECTORY_NAME,gamesToDeleteUniqueNames);
    }

    @Override
    public void acceptGames(String... gamesToDeleteUniqueNames) {
        gameRepository.acceptGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToDeleteUniqueNames) {
        gameRepository.cancelAcceptGames(gamesToDeleteUniqueNames);
    }

    @Override
    public Resource getGameRules(String gameName) {
        return fileService.loadAsResource(new StringBuilder(DEFAULT_GAME_RULES_DIRECTORY_NAME)
                        .append("/").append(gameName).append(".pdf").toString());
    }

    @Override
    public List<String> getAllGamesStatus() {
        return GameStatus.getNames();
    }
}
