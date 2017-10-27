package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;

import java.util.List;

@Component
public class TournamentsRepositoryImpl implements TournamentsRepository {
    private final TournamentRepository tournamentRepository;
    private final Searcher searcher;

    @Autowired
    public TournamentsRepositoryImpl(TournamentRepository tournamentRepository, Searcher getPageAssistant) {
        this.tournamentRepository = tournamentRepository;
        this.searcher = getPageAssistant;
    }

    @Override
    @Transactional
    public Page getPageOfTournaments(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return searcher
                .select(
                        new Field("name", "name"),
                        new Field("playersNumber", "playersNumber"),
                        new Field("freeSlots", "freeSlots"),
                        new Field("maxPlayers", "maxPlayers"),
                        new Field("playersOnTableCount", "playersOnTableCount"),
                        new Field("dateOfStart", "dateOfStart"),
                        new Field("dateOfEnd", "dateOfEnd"),
                        new Field("address.city", "city"),
                        new Field("address.province", "province"),
                        new Field("game.name", "game"),
                        new Field("status", "status"),
                        new Field("banned", "banned")
                )
                .join(
                        new Join("address", "address"),
                        new Join("game", "game")
                )
                .from(Tournament.class)
                .where(searchCriteria)
                .groupBy("id", "address.city", "game.name","address.province")
                .execute("id",requestedPage);
    }

    @Override
    public void banTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.banTournamentsByUniqueNames(tournamentsToBanUniqueNames);
    }

    @Override
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        this.tournamentRepository.deleteParticipationByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        this.tournamentRepository.deleteOrganizationByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        List<Long> idsOfToursToDelete = this.tournamentRepository.selectIdsOfToursToDeleteByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        if(idsOfToursToDelete.size()>0){
            this.deleteTournamentInProgressionRelations(idsOfToursToDelete);
        }
        this.tournamentRepository.deleteTournamentsByUniqueNames(tournamentsToDeleteUniqueNames);
    }

    @Override
    public void deleteTournamentInProgressionRelations(List<Long> idsOfToursToDelete){
        List<Long> idsOfBattlesToDelete = this.tournamentRepository.selectIdsOfBattlesToDeleteByToursIds(idsOfToursToDelete);
        this.tournamentRepository.deletePlaysByBattlesIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteBattlesByIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteToursByIds(idsOfToursToDelete);
    }

    @Override
    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.unlockTournamentsByUniqueNames(tournamentsToBanUniqueNames);
    }

    @Override
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        this.tournamentRepository.acceptTournamentsByUniqueNames(tournamentsToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptationTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        this.tournamentRepository.cancelAcceptTournamentsByUniqueNames(tournamentsToCancelAcceptUniqueNames);
    }
}
