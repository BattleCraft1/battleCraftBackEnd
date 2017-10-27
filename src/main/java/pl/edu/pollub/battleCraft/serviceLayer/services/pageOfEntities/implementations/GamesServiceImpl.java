package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.GamesRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.interfaces.UniqueNamesValidator;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.GamesService;

import java.util.List;

@Service
public class GamesServiceImpl implements GamesService {
    private final GamesRepository gamesRepository;
    private final GameRepository gameRepository;
    private final UniqueNamesValidator uniqueNamesValidator;

    @Autowired
    public GamesServiceImpl(GamesRepository gamesRepository, GameRepository gameRepository, UniqueNamesValidator uniqueNamesValidator) {
        this.gamesRepository = gamesRepository;
        this.gameRepository = gameRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
    }

    @Override
    public Page getPageOfGames(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return gamesRepository.getPageOfGames(searchCriteria, requestedPage);
    }

    @Override
    public void banGames(String... gamesToBanUniqueNames) {
        gamesRepository.banGames(gamesToBanUniqueNames);
    }

    @Override
    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gamesRepository.unlockGames(gamesToUnlockUniqueNames);
    }

    @Override
    public void deleteGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToDeleteUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.deleteGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void acceptGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToAcceptUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.acceptGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToRejectUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToReject(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.cancelAcceptGames(gamesToDeleteUniqueNames);
    }
}
