package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedTournamentRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Alias;
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
                        new Field("name", "name", Projections::property),
                        new Field("playersNumber", "playersNumber", Projections::property),
                        new Field("freeSlots", "freeSlots", Projections::property),
                        new Field("maxPlayers", "maxPlayers", Projections::property),
                        new Field("dateOfStart", "dateOfStart", Projections::property),
                        new Field("dateOfEnd", "dateOfEnd", Projections::property),
                        new Field("address.city", "city", Projections::property),
                        new Field("province.location", "province", Projections::property),
                        new Field("game.name", "game", Projections::property),
                        new Field("status", "status", Projections::property),
                        new Field("banned", "banned", Projections::property)
                )
                .createAliases(
                        new Alias("participants", "participants"),
                        new Alias("address", "address"),
                        new Alias("address.province", "province"),
                        new Alias("game", "game")
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
