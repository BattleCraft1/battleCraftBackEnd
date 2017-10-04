package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedGameRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.TournamentRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class ExtendedGameRepositoryImpl implements ExtendedGameRepository{
    private final GameRepository gameRepository;
    private final TournamentRepository tournamentRepository;
    private final OrganizerRepository organizerRepository;
    private final GetPageAssistant getPageAssistant;

    @Autowired
    public ExtendedGameRepositoryImpl(GameRepository gameRepository, TournamentRepository tournamentRepository,
                                      OrganizerRepository organizerRepository, GetPageAssistant getPageAssistant) {
        this.gameRepository = gameRepository;
        this.tournamentRepository = tournamentRepository;
        this.organizerRepository = organizerRepository;
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
        List<Long> idsOfBattlesToDelete = this.tournamentRepository.selectIdsOfBattlesToDeleteByToursIds(idsOfToursToDelete);
        this.tournamentRepository.deletePlaysByBattlesIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteBattlesByIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteToursByIds(idsOfToursToDelete);
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

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }
}
