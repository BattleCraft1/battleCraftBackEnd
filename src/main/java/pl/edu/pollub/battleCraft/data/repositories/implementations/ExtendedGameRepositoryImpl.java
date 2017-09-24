package pl.edu.pollub.battleCraft.data.repositories.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedGameRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.GameRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class ExtendedGameRepositoryImpl implements ExtendedGameRepository{
    private final GameRepository gameRepository;
    private final GetPageAssistant getPageAssistant;

    @Autowired
    public ExtendedGameRepositoryImpl(GameRepository gameRepository, GetPageAssistant getPageAssistant) {
        this.gameRepository = gameRepository;
        this.getPageAssistant = getPageAssistant;
    }

    @Override
    @Transactional
    public Page getPageOfGames(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return getPageAssistant
                .select(
                        new Field("name", "name"),
                        new Field("tournamentsNumber", "tournamentsNumber"),
                        new Field("creator.name", "creatorName"),
                        new Field("status", "status"),
                        new Field("banned", "banned"),
                        new Field("dateOfCreation", "dateOfCreation")
                )
                .createAliases(
                        new Field("creator", "creator")
                )
                .from(Game.class)
                .where(searchCriteria)
                .groupBy("creator.name","id")
                .execute(requestedPage);
    }

    @Override
    public void banGames(String... gamesToBanUniqueNames) {
        gameRepository.banGames(gamesToBanUniqueNames);
    }

    @Override
    public void deleteGames(String... gamesToDeleteUniqueNames) {
        gameRepository.deleteParticipationInTournamentsOfGames(gamesToDeleteUniqueNames);
        gameRepository.deleteOrganizationOfTournamentsOfGames(gamesToDeleteUniqueNames);
        gameRepository.deleteTournamentsOfGames(gamesToDeleteUniqueNames);
        gameRepository.deleteGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gameRepository.unlockGames(gamesToUnlockUniqueNames);
    }

    @Override
    public void acceptGames(String... gamesToAcceptUniqueNames) {
        gameRepository.acceptGames(gamesToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames) {
        gameRepository.cancelAcceptGames(gamesToCancelAcceptUniqueNames);
    }

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }
}
