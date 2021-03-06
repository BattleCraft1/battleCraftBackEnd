package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.PageNotFoundException;

import java.util.List;

@Component
public class TournamentsRepository{
    private final TournamentRepository tournamentRepository;
    private final Searcher searcher;

    @Autowired
    public TournamentsRepository(TournamentRepository tournamentRepository, Searcher searcher) {
        this.tournamentRepository = tournamentRepository;
        this.searcher = searcher;
    }

    @Transactional(rollbackFor = {AnyObjectNotFoundException.class,PageNotFoundException.class})
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
                        new Join("addressOwnership.address", "address"),
                        new Join("game", "game")
                )
                .from(Tournament.class)
                .where(searchCriteria)
                .groupBy("id", "address.city", "game.name","address.province")
                .execute("id",requestedPage);
    }

    @Transactional
    public void banTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.banTournamentsByUniqueNames(tournamentsToBanUniqueNames);
    }

    @Transactional
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        this.tournamentRepository.deleteParticipationByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        this.tournamentRepository.deleteOrganizationByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        List<Long> idsOfToursToDelete = this.tournamentRepository.selectIdsOfTurnsToDeleteByTournamentsUniqueNames(tournamentsToDeleteUniqueNames);
        if(idsOfToursToDelete.size()>0){
            this.deleteTournamentInProgressionRelations(idsOfToursToDelete);
        }
        this.tournamentRepository.deleteTournamentsByUniqueNames(tournamentsToDeleteUniqueNames);
        this.tournamentRepository.deleteRelatedAddresses(tournamentsToDeleteUniqueNames);
    }

    @Transactional
    void deleteTournamentInProgressionRelations(List<Long> idsOfToursToDelete){
        List<Long> idsOfBattlesToDelete = this.tournamentRepository.selectIdsOfBattlesToDeleteByTurnsIds(idsOfToursToDelete);
        this.tournamentRepository.deletePlaysByBattlesIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteBattlesByIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteTurnsByIds(idsOfToursToDelete);
    }

    @Transactional
    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.unlockTournamentsByUniqueNames(tournamentsToBanUniqueNames);
    }

    @Transactional
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        this.tournamentRepository.acceptTournamentsByUniqueNames(tournamentsToAcceptUniqueNames);
    }

    @Transactional
    public void cancelAcceptationTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        this.tournamentRepository.cancelAcceptTournamentsByUniqueNames(tournamentsToCancelAcceptUniqueNames);
    }
}
