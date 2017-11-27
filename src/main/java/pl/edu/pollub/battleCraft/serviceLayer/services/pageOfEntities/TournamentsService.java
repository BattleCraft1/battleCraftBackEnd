package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.TournamentsRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class TournamentsService {

    private final TournamentsRepository tournamentsRepository;
    private final TournamentRepository tournamentRepository;
    private final UniqueNamesValidator uniqueNamesValidator;
    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public TournamentsService(TournamentsRepository tournamentsRepository, TournamentRepository tournamentRepository, UniqueNamesValidator uniqueNamesValidator, AuthorityRecognizer roleRecognizer) {
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentRepository = tournamentRepository;
        this.uniqueNamesValidator = uniqueNamesValidator;
        this.authorityRecognizer = roleRecognizer;
    }

    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        authorityRecognizer.modifySearchCriteriaForCurrentUserRole(searchCriteria);
        return tournamentsRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    public void banTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    public void deleteTournaments(String... tournamentsToDeleteUniqueNames) {
        List<String> validUniqueNames = authorityRecognizer.checkIfCurrentUserCanDeleteTournaments(tournamentsToDeleteUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToDelete(validUniqueNames,tournamentsToDeleteUniqueNames);

        tournamentsRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsToAcceptUniqueNames(tournamentsToAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToAcceptUniqueNames);

        tournamentsRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        List<String> validUniqueNames = tournamentRepository.selectTournamentsToRejectUniqueNames(tournamentsToCancelAcceptUniqueNames);

        uniqueNamesValidator.validateUniqueNamesElementsToAccept(validUniqueNames,tournamentsToCancelAcceptUniqueNames);

        tournamentsRepository.cancelAcceptationTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
