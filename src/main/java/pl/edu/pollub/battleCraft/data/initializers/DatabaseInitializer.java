package pl.edu.pollub.battleCraft.data.initializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.*;
import pl.edu.pollub.battleCraft.data.entities.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.TournamentRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TournamentRepository tournamentRepository;

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

    private UserAccount testUser1;
    private UserAccount testUser2;
    private UserAccount testUser3;
    private UserAccount testUser4;
    private UserAccount testUser5;
    private UserAccount testUser6;
    private UserAccount testUser7;
    private UserAccount testUser8;
    private UserAccount testUser9;
    private UserAccount testUser10;

    private Participation testParticipation1;
    private Participation testParticipation2;
    private Participation testParticipation3;
    private Participation testParticipation4;
    private Participation testParticipation5;
    private Participation testParticipation6;
    private Participation testParticipation7;
    private Participation testParticipation8;
    private Participation testParticipation9;
    private Participation testParticipation10;
    private Participation testParticipation11;
    private Participation testParticipation12;
    private Participation testParticipation13;
    private Participation testParticipation14;
    private Participation testParticipation15;
    private Participation testParticipation16;
    private Participation testParticipation17;
    private Participation testParticipation18;
    private Participation testParticipation19;
    private Participation testParticipation20;

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

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Autowired
    public DatabaseInitializer(TournamentRepository tournamentRepository) {
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        tournamentRepository.deleteAll();

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

        testUser1 = new UserAccount("Dawid");
        testUser2 = new UserAccount("Andrzej");
        testUser3 = new UserAccount("Bartek");
        testUser4 = new UserAccount("Filip");
        testUser5 = new UserAccount("Albert");
        testUser6 = new UserAccount("Tomek");
        testUser7 = new UserAccount("Jarek");
        testUser8 = new UserAccount("Anastazy");
        testUser9 = new UserAccount("Wincent");
        testUser10 = new UserAccount("Krzysiek");

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

        try {
        testTournament1 = new Tournament("Tournament1", TournamentClass.CHALLENGER, 6, 3,
                format.parse("08-01-2017 13:05:00"), TournamentStatus.ACCEPTED,false);
        testTournament2 = new Tournament("Tournament2xxxxxxxx",TournamentClass.LOCAL, 8, 4,
                format.parse("09-02-2018 14:11:00"), TournamentStatus.NEW,false);
        testTournament3 = new Tournament("Tournament3",TournamentClass.MASTER, 6, 3,
                format.parse("12-03-2017 15:15:00"), TournamentStatus.NEW, true);
        testTournament4 = new Tournament("Tournament4",TournamentClass.CHALLENGER, 10, 5,
                format.parse("25-04-2018 16:25:00"), TournamentStatus.FINISHED, false);
        testTournament5 = new Tournament("Tournament5",TournamentClass.CHALLENGER, 8, 4,
                format.parse("13-05-2017 11:24:00"), TournamentStatus.ACCEPTED,false);
        testTournament6 = new Tournament("Tournament6",TournamentClass.MASTER, 6, 3,
                format.parse("11-11-2018 10:13:00"), TournamentStatus.FINISHED,true);
        testTournament7 = new Tournament("Tournament7",TournamentClass.CHALLENGER, 4, 2,
                format.parse("01-12-2017 11:06:00"), TournamentStatus.ACCEPTED,false);
        testTournament8 = new Tournament("Tournament8",TournamentClass.LOCAL, 20, 10,
                format.parse("02-06-2018 12:12:00"), TournamentStatus.FINISHED,false);
        testTournament9 = new Tournament("Tournament9",TournamentClass.MASTER, 8, 4,
                format.parse("13-07-2017 17:17:00"), TournamentStatus.ACCEPTED,false);
        testTournament10 = new Tournament("Tournament10",TournamentClass.LOCAL, 6, 3,
                format.parse("26-08-2018 18:05:00"), TournamentStatus.IN_PROGRESS,false);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        testParticipation1 = new Participation(testUser1,testTournament1);
        testParticipation2 = new Participation(testUser2,testTournament1);
        testParticipation3 = new Participation(testUser3,testTournament2);
        testParticipation4 = new Participation(testUser4,testTournament2);
        testParticipation5 = new Participation(testUser5,testTournament3);
        testParticipation6 = new Participation(testUser6,testTournament3);
        testParticipation7 = new Participation(testUser7,testTournament4);
        testParticipation8 = new Participation(testUser8,testTournament4);
        testParticipation9 = new Participation(testUser9,testTournament5);
        testParticipation10 = new Participation(testUser10,testTournament5);
        testParticipation11 = new Participation(testUser1,testTournament6);
        testParticipation12 = new Participation(testUser2,testTournament6);
        testParticipation13 = new Participation(testUser3,testTournament7);
        testParticipation14 = new Participation(testUser4,testTournament7);
        testParticipation15 = new Participation(testUser5,testTournament8);
        testParticipation16 = new Participation(testUser6,testTournament8);
        testParticipation17 = new Participation(testUser7,testTournament9);
        testParticipation18 = new Participation(testUser8,testTournament9);
        testParticipation19 = new Participation(testUser9,testTournament10);
        testParticipation20 = new Participation(testUser10,testTournament10);

        testTournament1.setParticipants(Arrays.asList(testParticipation1,testParticipation2));
        testTournament2.setParticipants(Arrays.asList(testParticipation3,testParticipation4));
        testTournament3.setParticipants(Arrays.asList(testParticipation5,testParticipation6));
        testTournament4.setParticipants(Arrays.asList(testParticipation7,testParticipation8));
        testTournament5.setParticipants(Arrays.asList(testParticipation9,testParticipation10));
        testTournament6.setParticipants(Arrays.asList(testParticipation11,testParticipation12));
        testTournament7.setParticipants(Arrays.asList(testParticipation13,testParticipation14));
        testTournament8.setParticipants(Arrays.asList(testParticipation15,testParticipation16));
        testTournament9.setParticipants(Arrays.asList(testParticipation17,testParticipation18));
        testTournament10.setParticipants(Arrays.asList(testParticipation19,testParticipation20));

        testUser1.setParticipatedTournaments(Arrays.asList(testParticipation1,testParticipation11));
        testUser2.setParticipatedTournaments(Arrays.asList(testParticipation2,testParticipation12));
        testUser3.setParticipatedTournaments(Arrays.asList(testParticipation3,testParticipation13));
        testUser4.setParticipatedTournaments(Arrays.asList(testParticipation4,testParticipation14));
        testUser5.setParticipatedTournaments(Arrays.asList(testParticipation5,testParticipation15));
        testUser6.setParticipatedTournaments(Arrays.asList(testParticipation6,testParticipation16));
        testUser7.setParticipatedTournaments(Arrays.asList(testParticipation7,testParticipation17));
        testUser8.setParticipatedTournaments(Arrays.asList(testParticipation8,testParticipation18));
        testUser9.setParticipatedTournaments(Arrays.asList(testParticipation9,testParticipation19));
        testUser10.setParticipatedTournaments(Arrays.asList(testParticipation10,testParticipation20));


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

}
