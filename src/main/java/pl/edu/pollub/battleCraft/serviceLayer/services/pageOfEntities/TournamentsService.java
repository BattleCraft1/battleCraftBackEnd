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
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.UniqueNamesValidator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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

    public Page getPageOfTournamentsParticipatedByUser(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        authorityRecognizer.modifySearchCriteriaForCurrentPlayer(searchCriteria);
        return tournamentsRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    public Page getPageOfTournamentsOrganizedByUser(Pageable requestedPage, List<SearchCriteria> searchCriteria) {
        authorityRecognizer.modifySearchCriteriaForCurrentOrganizer(searchCriteria);
        return tournamentsRepository.getPageOfTournaments(searchCriteria, requestedPage);
    }

    public void banTournaments(String... tournamentsToBanUniqueNames) {
        tournamentsRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    public void unlockTournaments(String... tournamentsToBanUniqueNames) {
        List<Tournament> tournaments  = tournamentRepository.findBannedTournamentsByUniqueNames(tournamentsToBanUniqueNames);

        List<String> namesOfTournamentsWhichCannotBeUnlock = new ArrayList<>();
        tournaments.forEach(
                tournament -> {
                    if(tournament.getGame().getStatus() == GameStatus.NEW || tournament.getGame().isBanned())
                        namesOfTournamentsWhichCannotBeUnlock.add(tournament.getName());
                }
        );

        List<String> validNames = tournaments.stream().map(tournament -> tournament.getName()).collect(Collectors.toList());
        validNames.removeAll(namesOfTournamentsWhichCannotBeUnlock);
        uniqueNamesValidator.validateUniqueNamesOfTournamentsToUnlock(validNames,tournamentsToBanUniqueNames);

        tournamentsRepository.unlockTournaments(tournamentsToBanUniqueNames); //only tournaments with accepted game can be unlocked
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
