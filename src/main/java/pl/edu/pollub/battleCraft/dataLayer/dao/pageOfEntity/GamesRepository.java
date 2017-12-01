package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;

import java.util.List;

@Component
public class GamesRepository {
    private final GameRepository gameRepository;
    private final TournamentRepository tournamentRepository;
    private final OrganizerRepository organizerRepository;
    private final Searcher searcher;
    private final TournamentsRepository tournamentsRepository;

    @Autowired
    public GamesRepository(GameRepository gameRepository, TournamentRepository tournamentRepository,
                           OrganizerRepository organizerRepository, Searcher searcher, TournamentsRepository tournamentsRepository) {
        this.gameRepository = gameRepository;
        this.tournamentRepository = tournamentRepository;
        this.organizerRepository = organizerRepository;
        this.searcher = searcher;
        this.tournamentsRepository = tournamentsRepository;
    }

    @Transactional
    public Page getPageOfGames(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return searcher
                .select(
                        new Field("name", "name"),
                        new Field("tournamentsNumber", "tournamentsNumber"),
                        new Field("status", "status"),
                        new Field("banned", "banned"),
                        new Field("dateOfCreation", "dateOfCreation")
                )
                .join()
                .from(Game.class)
                .where(searchCriteria)
                .execute("id",requestedPage);
    }

    public void banGames(String... gamesToBanUniqueNames) {
        gameRepository.banGamesByUniqueNames(gamesToBanUniqueNames);
        tournamentRepository.banTournamentsRelatedWithGame(gamesToBanUniqueNames);
    }

    public void deleteGames(String... gamesToDeleteUniqueNames) {
        List<Long> tournamentsToDeleteIds =
                tournamentRepository.selectTournamentsIdsByGameUniqueNames(gamesToDeleteUniqueNames);
        List<Long> idsOfOrganizers = this.organizerRepository.selectIdsOfOrganizersByTournamentsIds(tournamentsToDeleteIds);
        this.organizerRepository.deleteCreationOfGamesByOrganizersIds(idsOfOrganizers);
        this.tournamentRepository.deleteParticipationByTournamentsIds(tournamentsToDeleteIds);
        this.tournamentRepository.deleteOrganizationByTournamentsIds(tournamentsToDeleteIds);
        List<Long> idsOfToursToDelete = this.tournamentRepository.selectIdsOfToursToDeleteByTournamentsIds(tournamentsToDeleteIds);
        if(idsOfToursToDelete.size()>0) {
            tournamentsRepository.deleteTournamentInProgressionRelations(idsOfToursToDelete);
        }
        this.tournamentRepository.deleteTournamentsByIds(tournamentsToDeleteIds);
        gameRepository.deleteGamesByUniqueNames(gamesToDeleteUniqueNames);
    }

    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gameRepository.unlockGamesByUniqueNames(gamesToUnlockUniqueNames);
    }

    public void acceptGames(String... gamesToAcceptUniqueNames) {
        gameRepository.acceptGamesByUniqueNames(gamesToAcceptUniqueNames);
    }

    public void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames) {
        gameRepository.cancelAcceptGamesUniqueNames(gamesToCancelAcceptUniqueNames);
        tournamentRepository.banTournamentsRelatedWithGame(gamesToCancelAcceptUniqueNames);
    }
}
