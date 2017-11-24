package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import java.util.List;


@Service
public class TournamentsService {
    private final TournamentsRepository tournamentsRepository;
    private final TournamentRepository tournamentRepository;
    private final UniqueNamesValidator uniqueNamesValidator;

    @Autowired
    public TournamentsService(TournamentsRepository tournamentsRepository, TournamentRepository tournamentRepository, UniqueNamesValidator uniqueNamesValidator) {
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentRepository = tournamentRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
    }

    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        return tournamentsRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    public void banTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsNamesToDeleteUniqueNames(tournamentsToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,tournamentsToDeleteUniqueNames);

        tournamentsRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsNamesToAcceptUniqueNames(tournamentsToAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToAcceptUniqueNames);

        tournamentsRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsNamesToRejectUniqueNames(tournamentsToCancelAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToCancelAcceptUniqueNames);

        tournamentsRepository.cancelAcceptationTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
