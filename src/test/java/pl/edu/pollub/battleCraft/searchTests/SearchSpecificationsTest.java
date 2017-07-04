package pl.edu.pollub.battleCraft.searchTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.repositories.TournamentRepository;
import pl.edu.pollub.battleCraft.searchSpecyfications.SearchSpecification;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class SearchSpecificationsTest {

    @Autowired
    TournamentRepository tournamentRepository;

    Tournament testTournament1;
    Tournament testTournament2;

    @Before
    public void createTestTournaments() {
        testTournament1 = new Tournament("Tournament1", (short)5, (short)10, new Date(), true, null, null);
        testTournament2 = new Tournament("Tournament2", (short)10, (short)5, new Date(), true, null, null);

        tournamentRepository.save(testTournament1);
        tournamentRepository.save(testTournament2);
    }

    @Test
    public void searchExampleTournament() {
        List<SearchCriteria> searchCriteria=new ArrayList<>();
        searchCriteria.add(new SearchCriteria("name",":","Tourn%"));
        SearchSpecification specfication = new SearchSpecification(searchCriteria);

        Pageable pageable=new PageRequest(1,1);

        Page<Tournament> results = tournamentRepository.findAll(specfication,pageable);

        System.out.println(testTournament1.toString());
        results.forEach(tournament -> System.out.println(tournament.toString()));

    }
}
