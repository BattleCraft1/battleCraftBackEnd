package pl.edu.pollub.battleCraft.data.initializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Address.Province;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.Tournament.TournamentBuilder;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.entities.User.UserBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.OrganizerBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.PlayerBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
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

    private final TournamentBuilder tournamentBuilder = new TournamentBuilder();
    private final PlayerBuilder playerBuilder = new PlayerBuilder();
    private final OrganizerBuilder organizerBuilder = new OrganizerBuilder();
    private final UserBuilder userBuilder = new UserBuilder();

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

        Province testProvince1 = new Province("lubelskie");
        Province testProvince2 = new Province("dolnośląskie");
        Province testProvince3 = new Province("małopolskie");
        Province testProvince4 = new Province("śląskie");
        Province testProvince5 = new Province("zachodiopomorskie");
        Province testProvince6 = new Province("wielkopolskie");
        Province testProvince7 = new Province("opolskie");
        Province testProvince8 = new Province("łódzkie");
        Province testProvince9 = new Province("podlaskie");

        Address testAddress1 = new Address(testProvince1,"Lublin","Nadbystrzycka","20-501");
        Address testAddress2 = new Address(testProvince1,"Zamość","1 Maja","30-301");
        Address testAddress3 = new Address(testProvince2,"Wrocław","Krańcowa","40-301");
        Address testAddress4 = new Address(testProvince3,"Kraków","Ostatnia","50-301");
        Address testAddress5 = new Address(testProvince4,"Katowice","Szara","60-301");
        Address testAddress6 = new Address(testProvince5,"Szczecin","Nudna","70-301");
        Address testAddress7 = new Address(testProvince6,"Poznań","Smutna","80-301");
        Address testAddress8 = new Address(testProvince7,"Opole","Długa","32-302");
        Address testAddress9 = new Address(testProvince8,"Łódź","Czarna","33-303");
        Address testAddress10 = new Address(testProvince9,"Białystok","Rozpaczy","40-304");

        Player testUser1 = playerBuilder.create("Bartek","Nowogrodzki","bart2123",
                        "bart2123@gmail.com","abc123")
                        .from(testAddress1)
                        .withPhoneNumber("123123123").build();
        Player testUser2 = playerBuilder.create("Andrzej","Bartoszewski","art2123",
                "art2123@gmail.com","abc123")
                .from(testAddress2)
                .withPhoneNumber("123123123").build();
        Player testUser3 = playerBuilder.create("Artur","Partoszewski","part2123",
                "part2123@gmail.com","abc123")
                .from(testAddress3)
                .withPhoneNumber("123123123").build();
        Player testUser4 = playerBuilder.create("Jurek","Jurkowski","jur2123",
                "jur2123@gmail.com","abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Player testUser5 = playerBuilder.create("Anna","Bielec","biel2123",
                "biel2123@gmail.com","abc123")
                .from(testAddress5).build();
        Player testUser6 = playerBuilder.create("Tomasz","Blawucki","blaw2123",
                "blawl2123@gmail.com","abc123")
                .from(testAddress5).build();
        Organizer testUser7 = organizerBuilder.create("Jurek","Jurkowski","jur2123",
                "jur2123@gmail.com","abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Organizer testUser8 = organizerBuilder.create("Lukasz","Depta","dept2123",
                "dept2123@gmail.com","abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Organizer testUser9 = organizerBuilder.create("Albert","Kwasny","kwas2123",
                "kwas2123@gmail.com","abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Organizer testUser10 = organizerBuilder.create("Anastazja","Nijaka","nijak2123",
                "nijak2123@gmail.com","abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();


        testGame1=new Game("Warhammer");
        testGame2=new Game("Star wars");
        testGame3=new Game("Warhammer 40k");
        testGame4=new Game("Cyber punk");
        testGame5=new Game("Heroes");
        testGame6=new Game("Lord of the rings");

        try {
        testTournamentx = new Tournament("Tournament1", , 6, 3,
                format.parse("08-01-2017 13:05:00"), format.parse("09-01-2017 14:05:00"),
                TournamentStatus.ACCEPTED,false);
            testTournament1 = tournamentBuilder.createTournament("Tournament1",6,3,TournamentClass.CHALLENGER)
        testTournament2 = new Tournament("Tournament2xxxxxxxx",TournamentClass.LOCAL, 8, 4,
                format.parse("09-02-2018 14:11:00"), format.parse("11-02-2017 14:05:00"),
                TournamentStatus.NEW,false);
        testTournament3 = new Tournament("Tournament3",TournamentClass.MASTER, 6, 3,
                format.parse("12-03-2017 15:15:00"), format.parse("12-03-2017 16:05:00"),
                TournamentStatus.NEW, true);
        testTournament4 = new Tournament("Tournament4",TournamentClass.CHALLENGER, 10, 5,
                format.parse("25-04-2018 16:25:00"), format.parse("27-04-2018 16:05:00"),
                TournamentStatus.FINISHED, false);
        testTournament5 = new Tournament("Tournament5",TournamentClass.CHALLENGER, 8, 4,
                format.parse("13-05-2017 11:24:00"), format.parse("15-05-2018 16:05:00"),
                TournamentStatus.ACCEPTED,false);
        testTournament6 = new Tournament("Tournament6",TournamentClass.MASTER, 6, 3,
                format.parse("11-11-2018 10:13:00"), format.parse("15-11-2018 10:13:00"),
                TournamentStatus.FINISHED,true);
        testTournament7 = new Tournament("Tournament7",TournamentClass.CHALLENGER, 4, 2,
                format.parse("01-12-2017 11:06:00"), format.parse("02-12-2017 11:06:00"),
                TournamentStatus.ACCEPTED,false);
        testTournament8 = new Tournament("Tournament8",TournamentClass.LOCAL, 20, 10,
                format.parse("02-06-2018 12:12:00"),format.parse("03-06-2018 12:12:00"),
                TournamentStatus.FINISHED,false);
        testTournament9 = new Tournament("Tournament9",TournamentClass.MASTER, 8, 4,
                format.parse("13-07-2017 17:17:00"), format.parse("16-07-2017 17:17:00"),
                TournamentStatus.ACCEPTED,false);
        testTournament10 = new Tournament("Tournament10",TournamentClass.LOCAL, 6, 3,
                format.parse("26-08-2018 18:05:00"), format.parse("26-08-2018 18:05:00"),
                TournamentStatus.IN_PROGRESS,false);
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
