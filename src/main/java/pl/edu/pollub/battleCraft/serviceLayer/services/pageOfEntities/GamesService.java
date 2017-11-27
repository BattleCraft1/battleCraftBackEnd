package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.GamesRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import java.util.Collections;
import java.util.List;

@Service
public class GamesService {
    private final GamesRepository gamesRepository;
    private final GameRepository gameRepository;
    private final UniqueNamesValidator uniqueNamesValidator;
    private final AuthorityRecognizer roleRecognizer;

    @Autowired
    public GamesService(GamesRepository gamesRepository, GameRepository gameRepository, UniqueNamesValidator uniqueNamesValidator, AuthorityRecognizer roleRecognizer) {
        this.gamesRepository = gamesRepository;
        this.gameRepository = gameRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
        this.roleRecognizer = roleRecognizer;
    }

    public Page getPageOfGames(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        roleRecognizer.modifySearchCriteriaForCurrentUserRole(searchCriteria);
        return gamesRepository.getPageOfGames(searchCriteria, requestedPage);
    }

    public void banGames(String... gamesToBanUniqueNames) {
        gamesRepository.banGames(gamesToBanUniqueNames);
    }

    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gamesRepository.unlockGames(gamesToUnlockUniqueNames);
    }

    public void deleteGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToDeleteUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.deleteGames(gamesToDeleteUniqueNames);
    }

    public void acceptGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToAcceptUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.acceptGames(gamesToDeleteUniqueNames);
    }

    public void cancelAcceptGames(String... gamesToDeleteUniqueNames) {
        List<String> validUniqueNames = gameRepository.selectGamesToRejectUniqueNames(gamesToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToReject(validUniqueNames,gamesToDeleteUniqueNames);

        gamesRepository.cancelAcceptGames(gamesToDeleteUniqueNames);
    }
}
