package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.TournamentsService;

import java.util.List;


@Service
public class TournamentsServiceImpl implements TournamentsService {
    private final TournamentsRepository tournamentRepository;

    @Autowired
    public TournamentsServiceImpl(TournamentsRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return tournamentRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    @Override
    public void banTournaments(String... tournamentsToBanUniqueNames) {
        tournamentRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        tournamentRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        tournamentRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    @Override
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        tournamentRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        tournamentRepository.cancelAcceptTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
