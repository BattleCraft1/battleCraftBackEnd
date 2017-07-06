package pl.edu.pollub.battleCraft.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.repositories.TournamentRepository;
import pl.edu.pollub.battleCraft.searchSpecyfications.SearchSpecification;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;
import pl.edu.pollub.battleCraft.services.TournamentService;

import java.util.List;


@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Page getPageOfTournaments(Pageable requestedPage, List<SearchCriteria> searchCriteria){

        return tournamentRepository.findAll(new SearchSpecification<>(searchCriteria),requestedPage);
    }
}
