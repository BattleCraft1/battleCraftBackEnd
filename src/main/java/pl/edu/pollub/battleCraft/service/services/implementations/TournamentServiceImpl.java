package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedTournamentRepository;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;
import pl.edu.pollub.battleCraft.service.services.interfaces.TournamentService;

import java.util.List;


@Service
public class TournamentServiceImpl implements TournamentService {
    private final ExtendedTournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(ExtendedTournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria){
        return tournamentRepository.getPageOfTournaments(new SearchSpecification<>(searchCriteria),requestedPage);
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

    @Override
    public List<String> getAllTournamentStatus() {
        return TournamentStatus.getNames();
    }


}
