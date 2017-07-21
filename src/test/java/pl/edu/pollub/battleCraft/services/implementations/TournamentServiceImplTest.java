package pl.edu.pollub.battleCraft.services.implementations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pollub.battleCraft.entities.Address;
import pl.edu.pollub.battleCraft.entities.Game;
import pl.edu.pollub.battleCraft.entities.Province;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.entities.enums.TournamentClass;
import pl.edu.pollub.battleCraft.repositories.TournamentRepository;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;
import pl.edu.pollub.battleCraft.services.TournamentService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@ComponentScan("pl.edu.pollub.battleCraft.services.implementations")
public class TournamentServiceImplTest {

    public TournamentServiceImplTest() throws ParseException {
    }

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentRepository tournamentRepository;


    private Tournament testTournament1;
    private Tournament testTournament2;
    private Tournament testTournament3;
    private Tournament testTournament4;
    private Tournament testTournament5;
    private Tournament testTournament6;
    private Tournament testTournament7;
    private Tournament testTournament8;
    private Tournament testTournament9;
    private Tournament testTournament10;

    private Address testAddress1;
    private Address testAddress2;
    private Address testAddress3;
    private Address testAddress4;
    private Address testAddress5;
    private Address testAddress6;
    private Address testAddress7;
    private Address testAddress8;
    private Address testAddress9;
    private Address testAddress10;

    private Province testProvince1;
    private Province testProvince2;
    private Province testProvince3;
    private Province testProvince4;
    private Province testProvince5;
    private Province testProvince6;
    private Province testProvince7;
    private Province testProvince8;
    private Province testProvince9;

    private Game testGame1;
    private Game testGame2;
    private Game testGame3;
    private Game testGame4;
    private Game testGame5;
    private Game testGame6;

    private DateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);

    @Before
    public void createTestTournaments() throws ParseException {

        testAddress1 = new Address("Lublin","Nadbystrzycka","20-501");
        testAddress2 = new Address("Zamość","1 Maja","30-301");
        testAddress3 = new Address("Wrocław","Krańcowa","40-301");
        testAddress4 = new Address("Kraków","Ostatnia","50-301");
        testAddress5 = new Address("Katowice","Szara","60-301");
        testAddress6 = new Address("Szczecin","Nudna","70-301");
        testAddress7 = new Address("Poznań","Smutna","80-301");
        testAddress8 = new Address("Opole","Długa","32-302");
        testAddress9 = new Address("Łódź","Czarna","33-303");
        testAddress10 = new Address("Białystok","Rozpaczy","40-304");

        testProvince1 = new Province("lubelskie");
        testProvince2 = new Province("dolnośląskie");
        testProvince3 = new Province("małopolskie");
        testProvince4 = new Province("śląskie");
        testProvince5 = new Province("zachodiopomorskie");
        testProvince6 = new Province("wielkopolskie");
        testProvince7 = new Province("opolskie");
        testProvince8 = new Province("łódzkie");
        testProvince9 = new Province("podlaskie");

        testAddress1.setProvince(testProvince1);
        testAddress2.setProvince(testProvince1);
        testProvince1.setAddresses(Arrays.asList(testAddress1,testAddress2));
        testAddress3.setProvince(testProvince2);
        testProvince2.setAddresses(Collections.singletonList(testAddress3));
        testAddress4.setProvince(testProvince3);
        testProvince3.setAddresses(Collections.singletonList(testAddress4));
        testAddress5.setProvince(testProvince4);
        testProvince4.setAddresses(Collections.singletonList(testAddress5));
        testAddress6.setProvince(testProvince5);
        testProvince5.setAddresses(Collections.singletonList(testAddress6));
        testAddress7.setProvince(testProvince6);
        testProvince6.setAddresses(Collections.singletonList(testAddress7));
        testAddress8.setProvince(testProvince7);
        testProvince7.setAddresses(Collections.singletonList(testAddress8));
        testAddress9.setProvince(testProvince8);
        testProvince8.setAddresses(Collections.singletonList(testAddress9));
        testAddress10.setProvince(testProvince9);
        testProvince9.setAddresses(Collections.singletonList(testAddress10));

        testTournament1 = new Tournament("Tournament1", TournamentClass.CHALLENGER, (short)6, (short)3, format.parse("13:05:00 08-01-2017"), true, false);
        testTournament2 = new Tournament("Tournament2",TournamentClass.LOCAL, (short)8, (short)4, format.parse("14:11:00 09-02-2018"), false, false);
        testTournament3 = new Tournament("Tournament3",TournamentClass.MASTER, (short)6, (short)3, format.parse("15:15:00 12-03-2017"), true, false);
        testTournament4 = new Tournament("Tournament4",TournamentClass.CHALLENGER, (short)10, (short)5, format.parse("16:25:00 25-04-2018"), true, false);
        testTournament5 = new Tournament("Tournament5",TournamentClass.CHALLENGER, (short)8, (short)4, format.parse("11:24:00 13-05-2017"), true, false);
        testTournament6 = new Tournament("Tournament6",TournamentClass.MASTER, (short)6, (short)3, format.parse("10:13:00 11-11-2018"), false, false);
        testTournament7 = new Tournament("Tournament7",TournamentClass.CHALLENGER, (short)4, (short)2, format.parse("11:06:00 01-12-2017"), true, false);
        testTournament8 = new Tournament("Tournament8",TournamentClass.LOCAL, (short)20, (short)10, format.parse("12:12:00 02-06-2018"), false, false);
        testTournament9 = new Tournament("Tournament9",TournamentClass.MASTER, (short)8, (short)4, format.parse("17:17:00 13-07-2017"), true, false);
        testTournament10 = new Tournament("Tournament10",TournamentClass.LOCAL, (short)6, (short)3, format.parse("18:05:00 26-08-2018"), false, false);


        testTournament1.setAddress(testAddress1);
        testAddress1.setAddressOwner(testTournament1);
        testTournament2.setAddress(testAddress2);
        testAddress2.setAddressOwner(testTournament2);
        testTournament3.setAddress(testAddress3);
        testAddress3.setAddressOwner(testTournament3);
        testTournament4.setAddress(testAddress4);
        testAddress4.setAddressOwner(testTournament4);
        testTournament5.setAddress(testAddress5);
        testAddress5.setAddressOwner(testTournament5);
        testTournament6.setAddress(testAddress6);
        testAddress6.setAddressOwner(testTournament6);
        testTournament7.setAddress(testAddress7);
        testAddress7.setAddressOwner(testTournament7);
        testTournament8.setAddress(testAddress8);
        testAddress8.setAddressOwner(testTournament8);
        testTournament9.setAddress(testAddress9);
        testAddress9.setAddressOwner(testTournament9);
        testTournament10.setAddress(testAddress10);
        testAddress10.setAddressOwner(testTournament10);

        testGame1=new Game("Warhammer");
        testGame2=new Game("Star wars");
        testGame3=new Game("Warhammer 40k");
        testGame4=new Game("Cyber punk");
        testGame5=new Game("Heroes");
        testGame6=new Game("Lord of the rings");

        testTournament1.setGame(testGame1);
        testTournament2.setGame(testGame2);
        testTournament3.setGame(testGame3);
        testTournament4.setGame(testGame4);
        testTournament5.setGame(testGame5);
        testTournament6.setGame(testGame6);
        testTournament7.setGame(testGame1);
        testTournament8.setGame(testGame2);
        testTournament9.setGame(testGame3);
        testTournament10.setGame(testGame4);

        testGame1.setTournaments(Arrays.asList(testTournament1,testTournament7));
        testGame2.setTournaments(Arrays.asList(testTournament2,testTournament8));
        testGame3.setTournaments(Arrays.asList(testTournament3,testTournament9));
        testGame4.setTournaments(Arrays.asList(testTournament4,testTournament10));
        testGame5.setTournaments(Collections.singletonList(testTournament5));
        testGame6.setTournaments(Collections.singletonList(testTournament6));

        tournamentRepository.save(testTournament1);
        tournamentRepository.save(testTournament2);
        tournamentRepository.save(testTournament3);
        tournamentRepository.save(testTournament4);
        tournamentRepository.save(testTournament5);
        tournamentRepository.save(testTournament6);
        tournamentRepository.save(testTournament7);
        tournamentRepository.save(testTournament8);
        tournamentRepository.save(testTournament9);
        tournamentRepository.save(testTournament10);

    }

    @Test
    public void searchAllTournamentsWithGameWahammerAfterSomeDate() throws ParseException {
        List<SearchCriteria> searchCriteria=new ArrayList<>();
        searchCriteria.add(new SearchCriteria(Arrays.asList("game","name"),":","Warhammer"));
        searchCriteria.add(new SearchCriteria(Collections.singletonList("dateOfStart"),">",format.parse("15:15:00 08-02-2017")));

        Pageable pageable=new PageRequest(0,10);

        Page results = tournamentService.getPageOfTournaments(pageable,searchCriteria);

        assertEquals(1, results.getTotalElements());
        assertEquals(Collections.singletonList(testTournament7), results.getContent());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void searchByFieldThatNotExist()
    {
        List<SearchCriteria> searchCriteria=new ArrayList<>();
        searchCriteria.add(new SearchCriteria(Collections.singletonList("testField"),":","Warhammer"));

        Pageable pageable=new PageRequest(0,10);

        tournamentService.getPageOfTournaments(pageable,searchCriteria);
    }
    //I wanted to create this tests but I do not know how to use aspects in tests
    @Test
    public void getMoreElementsThanAllowedPageSize()
    {

    }

    @Test
    public void getEmptyPage()
    {

    }

    @Test
    public void getAllTournamentsFromProvince()
    {
        List<SearchCriteria> searchCriteria=new ArrayList<>();
        searchCriteria.add(new SearchCriteria(Arrays.asList("address","province","location"),":","lubelskie"));

        Pageable pageable=new PageRequest(0,10);

        Page<Tournament> results = tournamentService.getPageOfTournaments(pageable,searchCriteria);

        assertEquals(2, results.getTotalElements());
        assertEquals(Arrays.asList(testTournament1,testTournament2), results.getContent());
    }

    @Test
    public void getAllChallengerActiveTournamentsWithSomeCountOfTables()
    {
        List<SearchCriteria> searchCriteria=new ArrayList<>();
        searchCriteria.add(new SearchCriteria(Collections.singletonList("active"),":",true));
        searchCriteria.add(new SearchCriteria(Collections.singletonList("tablesCount"),">",4));
        searchCriteria.add(new SearchCriteria(Collections.singletonList("tournamentClass"),":",TournamentClass.CHALLENGER));

        Pageable pageable=new PageRequest(0,10);

        Page<Tournament> results = tournamentService.getPageOfTournaments(pageable,searchCriteria);

        assertEquals(2, results.getTotalElements());
        assertEquals(Arrays.asList(testTournament4,testTournament5), results.getContent());
    }
}

