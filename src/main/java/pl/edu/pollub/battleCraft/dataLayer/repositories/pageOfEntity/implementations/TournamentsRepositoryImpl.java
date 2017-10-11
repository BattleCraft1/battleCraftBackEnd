package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class TournamentsRepositoryImpl implements TournamentsRepository {
    private final TournamentRepository tournamentRepository;
    private final GetPageAssistant getPageAssistant;

    @Autowired
    public TournamentsRepositoryImpl(TournamentRepository tournamentRepository, GetPageAssistant getPageAssistant) {
        this.tournamentRepository = tournamentRepository;
        this.getPageAssistant = getPageAssistant;
    }

    @Override
    @Transactional
    public Page getPageOfTournaments(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return getPageAssistant
                .select(
                        new Field("name", "name"),
                        new Field("playersNumber", "playersNumber"),
                        new Field("freeSlots", "freeSlots"),
                        new Field("maxPlayers", "maxPlayers"),
                        new Field("dateOfStart", "dateOfStart"),
                        new Field("dateOfEnd", "dateOfEnd"),
                        new Field("address.city", "city"),
                        new Field("address.province", "province"),
                        new Field("game.name", "game"),
                        new Field("status", "status"),
                        new Field("banned", "banned")
                )
                .join(
                        new Join("participants", "participants"),
                        new Join("address", "address"),
                        new Join("game", "game")
                )
                .from(Tournament.class)
                .where(searchCriteria)
                .groupBy("id", "address.city", "game.name")
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
        List<Long> idsOfBattlesToDelete = this.tournamentRepository.selectIdsOfBattlesToDeleteByToursIds(idsOfToursToDelete);
        this.tournamentRepository.deletePlaysByBattlesIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteBattlesByIds(idsOfBattlesToDelete);
        this.tournamentRepository.deleteToursByIds(idsOfToursToDelete);
        this.tournamentRepository.deleteTournamentsByUniqueNames(tournamentsToDeleteUniqueNames);
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
    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        this.tournamentRepository.cancelAcceptTournamentsByUniqueNames(tournamentsToCancelAcceptUniqueNames);
    }
}
