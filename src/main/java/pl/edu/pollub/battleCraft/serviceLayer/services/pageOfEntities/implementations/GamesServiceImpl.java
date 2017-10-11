package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.GamesRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.GamesService;

import java.util.List;

@Service
public class GamesServiceImpl implements GamesService {
    private final GamesRepository gameRepository;

    @Autowired
    public GamesServiceImpl(GamesRepository gameRepository) {
        this.gameRepository = gameRepository;
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
    }

    @Override
    public void acceptGames(String... gamesToDeleteUniqueNames) {
        gameRepository.acceptGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToDeleteUniqueNames) {
        gameRepository.cancelAcceptGames(gamesToDeleteUniqueNames);
    }
}
