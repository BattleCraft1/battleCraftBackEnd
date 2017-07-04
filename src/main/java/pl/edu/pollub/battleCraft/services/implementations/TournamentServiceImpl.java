package pl.edu.pollub.battleCraft.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.exceptions.PageNotFoundException;
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
    public Page<Tournament> getTournamentsFromPage(List<SearchCriteria> searchCriteria, Pageable requestedPage) throws PageNotFoundException, IllegalAccessException {
        if(requestedPage.getPageSize()>10)
            throw new IllegalAccessException("Your page must have less than 10 elements");

        Page<Tournament> fetchedPage=tournamentRepository.findAll(new SearchSpecification(searchCriteria),requestedPage);

        if(!fetchedPage.hasContent())
            throw new PageNotFoundException(Tournament.class.getName(),requestedPage.getPageNumber());

        return fetchedPage;
    }
}
