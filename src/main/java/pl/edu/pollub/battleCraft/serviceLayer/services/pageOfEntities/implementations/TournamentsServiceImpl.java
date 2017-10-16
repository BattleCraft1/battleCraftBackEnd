package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.UniqueNamesValidator;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.TournamentsService;

import java.util.List;


@Service
public class TournamentsServiceImpl implements TournamentsService {
    private final TournamentsRepository tournamentsRepository;
    private final TournamentRepository tournamentRepository;
    private final UniqueNamesValidator uniqueNamesValidator;

    @Autowired
    public TournamentsServiceImpl(TournamentsRepository tournamentsRepository, TournamentRepository tournamentRepository, UniqueNamesValidator uniqueNamesValidator) {
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentRepository = tournamentRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
    }

    @Override
    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return tournamentsRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    @Override
    public void banTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsToDeleteUniqueNames(tournamentsToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,tournamentsToDeleteUniqueNames);

        tournamentsRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    @Override
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsToAcceptUniqueNames(tournamentsToAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToAcceptUniqueNames);

        tournamentsRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsToRejectUniqueNames(tournamentsToCancelAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToCancelAcceptUniqueNames);

        tournamentsRepository.cancelAcceptationTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
