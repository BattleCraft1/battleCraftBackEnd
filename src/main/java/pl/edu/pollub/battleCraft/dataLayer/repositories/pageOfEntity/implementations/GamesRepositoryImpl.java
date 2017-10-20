package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.GamesRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces.SearchAssistant;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.TournamentsRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class GamesRepositoryImpl implements GamesRepository {
    private final GameRepository gameRepository;
    private final TournamentRepository tournamentRepository;
    private final OrganizerRepository organizerRepository;
    private final SearchAssistant getPageAssistant;
    private final TournamentsRepository tournamentsRepository;

    @Autowired
    public GamesRepositoryImpl(GameRepository gameRepository, TournamentRepository tournamentRepository,
                               OrganizerRepository organizerRepository, SearchAssistant getPageAssistant, TournamentsRepository tournamentsRepository) {
        this.gameRepository = gameRepository;
        this.tournamentRepository = tournamentRepository;
        this.organizerRepository = organizerRepository;
        this.getPageAssistant = getPageAssistant;
        this.tournamentsRepository = tournamentsRepository;
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
                .join(
                        new Join("creator", "creator")
                )
                .from(Game.class)
                .where(searchCriteria)
                .groupBy("creator.name","id")
                .execute("id",requestedPage);
    }

    @Override
    public void banGames(String... gamesToBanUniqueNames) {
        gameRepository.banGamesByUniqueNames(gamesToBanUniqueNames);
    }

    @Override
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

    @Override
    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gameRepository.unlockGamesByUniqueNames(gamesToUnlockUniqueNames);
    }

    @Override
    public void acceptGames(String... gamesToAcceptUniqueNames) {
        gameRepository.acceptGamesByUniqueNames(gamesToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames) {
        gameRepository.cancelAcceptGamesUniqueNames(gamesToCancelAcceptUniqueNames);
    }
}
