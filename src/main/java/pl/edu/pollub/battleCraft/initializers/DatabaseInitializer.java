package pl.edu.pollub.battleCraft.initializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.entities.Address;
import pl.edu.pollub.battleCraft.entities.Game;
import pl.edu.pollub.battleCraft.entities.Province;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.entities.enums.TournamentClass;
import pl.edu.pollub.battleCraft.repositories.TournamentRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

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
    private Province testProvince10;

    private Game testGame1;
    private Game testGame2;
    private Game testGame3;
    private Game testGame4;
    private Game testGame5;
    private Game testGame6;

    private DateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);

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
        testProvince2 = new Province("lubelskie");
        testProvince3 = new Province("dolnośląskie");
        testProvince4 = new Province("małopolskie");
        testProvince5 = new Province("śląskie");
        testProvince6 = new Province("zachodiopomorskie");
        testProvince7 = new Province("wielkopolskie");
        testProvince8 = new Province("opolskie");
        testProvince9 = new Province("łódzkie");
        testProvince10 = new Province("podlaskie");

        testProvince1.setAddress(testAddress1);
        testAddress1.setProvince(testProvince1);
        testProvince2.setAddress(testAddress2);
        testAddress2.setProvince(testProvince2);
        testProvince3.setAddress(testAddress3);
        testAddress3.setProvince(testProvince3);
        testProvince4.setAddress(testAddress4);
        testAddress4.setProvince(testProvince4);
        testProvince5.setAddress(testAddress5);
        testAddress5.setProvince(testProvince5);
        testProvince6.setAddress(testAddress6);
        testAddress6.setProvince(testProvince6);
        testProvince7.setAddress(testAddress7);
        testAddress7.setProvince(testProvince7);
        testProvince8.setAddress(testAddress8);
        testAddress8.setProvince(testProvince8);
        testProvince9.setAddress(testAddress9);
        testAddress9.setProvince(testProvince9);
        testProvince10.setAddress(testAddress10);
        testAddress10.setProvince(testProvince10);

        try {
        testTournament1 = new Tournament("Tournament1", TournamentClass.CHALLENGER, (short)6, (short)3, format.parse("13:05:00 08-01-2017"), true);testTournament2 = new Tournament("Tournament2",TournamentClass.LOCAL, (short)8, (short)4, format.parse("14:11:00 09-02-2018"), false);
        testTournament3 = new Tournament("Tournament3",TournamentClass.MASTER, (short)6, (short)3, format.parse("15:15:00 12-03-2017"), true);
        testTournament4 = new Tournament("Tournament4",TournamentClass.CHALLENGER, (short)10, (short)5, format.parse("16:25:00 25-04-2018"), true);
        testTournament5 = new Tournament("Tournament5",TournamentClass.CHALLENGER, (short)8, (short)4, format.parse("11:24:00 13-05-2017"), true);
        testTournament6 = new Tournament("Tournament6",TournamentClass.MASTER, (short)6, (short)3, format.parse("10:13:00 11-11-2018"), false);
        testTournament7 = new Tournament("Tournament7",TournamentClass.CHALLENGER, (short)4, (short)2, format.parse("11:06:00 01-12-2017"), true);
        testTournament8 = new Tournament("Tournament8",TournamentClass.LOCAL, (short)20, (short)10, format.parse("12:12:00 02-06-2018"), false);
        testTournament9 = new Tournament("Tournament9",TournamentClass.MASTER, (short)8, (short)4, format.parse("17:17:00 13-07-2017"), true);
        testTournament10 = new Tournament("Tournament10",TournamentClass.LOCAL, (short)6, (short)3, format.parse("18:05:00 26-08-2018"), false);
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
