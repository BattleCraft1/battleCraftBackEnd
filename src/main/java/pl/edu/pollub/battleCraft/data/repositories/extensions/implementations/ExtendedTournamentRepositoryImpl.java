package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedTournamentRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.TournamentRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class ExtendedTournamentRepositoryImpl implements ExtendedTournamentRepository {
    private final TournamentRepository tournamentRepository;
    private final GetPageAssistant getPageAssistant;

    @Autowired
    public ExtendedTournamentRepositoryImpl(TournamentRepository tournamentRepository, GetPageAssistant getPageAssistant) {
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
                        new Field("province.location", "province"),
                        new Field("game.name", "game"),
                        new Field("status", "status"),
                        new Field("banned", "banned")
                )
                .join(
                        new Join("participants", "participants"),
                        new Join("address", "address"),
                        new Join("address.province", "province"),
                        new Join("game", "game")
                )
                .from(Tournament.class)
                .where(searchCriteria)
                .groupBy("id", "address.city", "province.location", "game.name")
                .execute(requestedPage);
    }

    @Override
    public void banTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        this.tournamentRepository.deleteParticipationInTournaments(tournamentsToDeleteUniqueNames);
        this.tournamentRepository.deleteOrganizationOfTournaments(tournamentsToDeleteUniqueNames);
        List<Long> idsOfToursToDelete = this.tournamentRepository.selectIdsOfToursToDelete(tournamentsToDeleteUniqueNames);
        List<Long> idsOfBattlesToDelete = this.tournamentRepository.selectIdsOfBattlesToDelete(idsOfToursToDelete);
        this.tournamentRepository.deletePlaysOfTournaments(idsOfBattlesToDelete);
        this.tournamentRepository.deleteBattlesOfTournaments(idsOfBattlesToDelete);
        this.tournamentRepository.deleteToursOfTournaments(idsOfToursToDelete);
        this.tournamentRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    @Override
    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        this.tournamentRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        this.tournamentRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        this.tournamentRepository.cancelAcceptTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
