package pl.edu.pollub.battleCraft.dataLayer.initializers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserBuilder;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.OrganizerBuilder;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.PlayerBuilder;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TournamentRepository tournamentRepository;
    private final UserAccountRepository userAccountRepository;

    private final PlayerBuilder playerBuilder = new PlayerBuilder();
    private final OrganizerBuilder organizerBuilder = new OrganizerBuilder();
    private final UserBuilder userBuilder = new UserBuilder();

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Autowired
    public DatabaseInitializer(TournamentRepository tournamentRepository, UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        tournamentRepository.deleteAll();
        userAccountRepository.deleteAll();

        Address testAddress1 = new Address("lubelskie", "Lublin", "Nadbystrzycka", "20-501","");
        Address testAddress2 = new Address("dolnośląskie", "Zamość", "1 Maja", "30-301","");
        Address testAddress3 = new Address("małopolskie", "Wrocław", "Krańcowa", "40-301","");
        Address testAddress4 = new Address("śląskie", "Kraków", "Ostatnia", "50-301","");
        Address testAddress5 = new Address("zachodiopomorskie", "Katowice", "Szara", "60-301","");
        Address testAddress6 = new Address("opolskie", "Szczecin", "Nudna", "70-301","");
        Address testAddress7 = new Address("łódzkie", "Poznań", "Smutna", "80-301","");
        Address testAddress8 = new Address("podlaskie", "Opole", "Długa", "32-302","");
        Address testAddress9 = new Address("lubelskie", "Łódź", "Czarna", "33-303","");
        Address testAddress10 = new Address("dolnośląskie", "Białystok", "Rozpaczy", "40-304","");
        Address testAddress11 = new Address("małopolskie", "Lublin", "Nadbystrzycka", "20-501","");
        Address testAddress12 = new Address("dolnośląskie", "Zamość", "1 Maja", "30-301","");
        Address testAddress13 = new Address("śląskie", "Wrocław", "Krańcowa", "40-301","");
        Address testAddress14 = new Address("lubelskie", "Kraków", "Ostatnia", "50-301","");
        Address testAddress15 = new Address("małopolskie", "Katowice", "Szara", "60-301","");
        Address testAddress16 = new Address("łódzkie", "Szczecin", "Nudna", "70-301","");
        Address testAddress17 = new Address("podlaskie", "Poznań", "Smutna", "80-301","");
        Address testAddress18 = new Address("zachodiopomorskie", "Opole", "Długa", "32-302","");
        Address testAddress19 = new Address("małopolskie", "Łódź", "Czarna", "33-303","");
        Address testAddress20 = new Address("dolnośląskie", "Białystok", "Rozpaczy", "40-304","");
        Address testAddress21 = new Address("opolskie", "Łódź", "Rozpaczy", "40-304","");

        Player testUser1 = playerBuilder
                .create("Bartek", "Nowogrodzki", "bart2123", "bart2123@gmail.com", "abc123")
                .from(testAddress1)
                .withPhoneNumber("123123123").build();
        Player testUser2 = playerBuilder
                .create("Andrzej", "Bartoszewski", "art2123", "art2123@gmail.com", "abc123")
                .from(testAddress2)
                .withPhoneNumber("123123123").build();
        Player testUser3 = playerBuilder
                .create("Artur", "Partoszewski", "part2123", "part2123@gmail.com", "abc123")
                .from(testAddress3)
                .withPhoneNumber("123123123").build();
        Player testUser4 = playerBuilder
                .create("Jurek", "Jurkowski", "jur2123", "jur2123@gmail.com", "abc123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Player testUser5 = playerBuilder
                .create("Anna", "Bielec", "biel2123", "biel2123@gmail.com", "abc123")
                .from(testAddress5).build();
        Player testUser6 = playerBuilder
                .create("Tomasz", "Blawucki", "blaw2123", "blawl2123@gmail.com", "abc123")
                .from(testAddress6).build();
        Organizer testUser7 = organizerBuilder
                .create("Filip", "Begiello", "beg2123", "beg2123@gmail.com", "abc123")
                .from(testAddress7)
                .withPhoneNumber("123123123").build();
        Organizer testUser8 = organizerBuilder
                .create("Lukasz", "Depta", "dept2123", "dept2123@gmail.com", "abc123")
                .from(testAddress8)
                .withPhoneNumber("123123123").build();
        Organizer testUser9 = organizerBuilder
                .create("Albert", "Kwasny", "kwas2123", "kwas2123@gmail.com", "abc123")
                .from(testAddress9)
                .withPhoneNumber("123123123").build();
        Organizer testUser10 = organizerBuilder
                .create("Anastazja", "Nijaka", "nijak2123", "nijak2123@gmail.com", "abc123")
                .from(testAddress10)
                .withPhoneNumber("123123123").build();

        UserAccount testUser11 = userBuilder
                .create("Pawel", "Maziarczuk", "mazi2123", "mazi2123@gmail.com", "abc123")
                .from(testAddress21)
                .withPhoneNumber("123123123").build();


        Game testGame1 = testUser8.createGame("Warhammer");
        testGame1.setStatus(GameStatus.ACCEPTED);

        try {
            testUser7.startOrganizeTournament("Tournament1", 3,2)
                    .with(testUser8)
                    .in(testAddress11)
                    .withGame( testGame1)
                    .startAt(format.parse("08-01-2018 13:05:00"))
                    .endingIn(format.parse("09-01-2018 14:05:00"))
                    .inviteParticipants(testUser1,testUser2,testUser3,testUser4)
                    .finishOrganize();

            Tournament testTournament1 = testUser7.startTournament("Tournament1",2);
            testUser7.setRandomPlayersOnTableInFirstTour("Tournament1",0);
            testUser7.setRandomPlayersOnTableInFirstTour("Tournament1",1);
            testUser7.setPointsOnTable("Tournament1",0,5);
            testUser7.setPointsOnTable("Tournament1",1,8);
            testUser7.prepareNextTour("Tournament1");
            testUser7.setPointsOnTable("Tournament1",0,8);
            testUser7.setPointsOnTable("Tournament1",1,10);
            testUser7.finishTournament("Tournament1");

            testUser10.startOrganizeTournament("Tournament2", 4,2)
                    .in(testAddress12)
                    .withGame( testGame1)
                    .startAt(format.parse("09-02-2018 14:11:00"))
                    .endingIn(format.parse("11-02-2018 14:05:00"))
                    .inviteParticipants(testUser2,testUser3,testUser5,testUser9)
                    .finishOrganize();

            Tournament testTournament2 = testUser10.startTournament("Tournament2",2);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament2",0);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament2",1);
            testUser10.setPointsOnTable("Tournament2",0,5);
            testUser10.setPointsOnTable("Tournament2",1,8);
            testUser10.prepareNextTour("Tournament2");
            testUser10.setPointsOnTable("Tournament2",0,8);
            testUser10.setPointsOnTable("Tournament2",1,10);
            testUser10.finishTournament("Tournament2");

            testUser7.startOrganizeTournament("Tournament3", 3,2)
                    .with(testUser9)
                    .in(testAddress13)
                    .withGame( testGame1)
                    .startAt(format.parse("12-03-2018 15:15:00"))
                    .endingIn(format.parse("12-03-2018 16:05:00"))
                    .inviteParticipants(testUser1,testUser3,testUser5,testUser7)
                    .finishOrganize();

            Tournament testTournament3 = testUser9.startTournament("Tournament3",2);
            testUser9.setRandomPlayersOnTableInFirstTour("Tournament3",0);
            testUser9.setRandomPlayersOnTableInFirstTour("Tournament3",1);
            testUser9.setPointsOnTable("Tournament3",0,5);
            testUser9.setPointsOnTable("Tournament3",1,8);
            testUser9.prepareNextTour("Tournament3");
            testUser9.setPointsOnTable("Tournament3",0,8);
            testUser9.setPointsOnTable("Tournament3",1,10);
            testUser9.finishTournament("Tournament3");

            testUser10.startOrganizeTournament("Tournament4",  5,2)
                    .in(testAddress14)
                    .withGame( testGame1)
                    .startAt(format.parse("25-04-2018 16:25:00"))
                    .endingIn(format.parse("27-04-2018 16:05:00"))
                    .inviteParticipants(testUser2,testUser4,testUser6,testUser8)
                    .finishOrganize();

            Tournament testTournament4 = testUser10.startTournament("Tournament4",2);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament4",0);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament4",1);
            testUser10.setPointsOnTable("Tournament4",0,5);
            testUser10.setPointsOnTable("Tournament4",1,8);
            testUser10.prepareNextTour("Tournament4");
            testUser10.setPointsOnTable("Tournament4",0,8);
            testUser10.setPointsOnTable("Tournament4",1,10);
            testUser10.finishTournament("Tournament4");


            testUser9.startOrganizeTournament("Tournament5", 4,2)
                    .with(testUser7)
                    .in(testAddress15)
                    .withGame( testGame1)
                    .startAt(format.parse("13-05-2018 11:24:00"))
                    .endingIn(format.parse("15-05-2018 16:05:00"))
                    .inviteParticipants(testUser5,testUser10)
                    .finishOrganize();

            Tournament testTournament5 = testUser9.startTournament("Tournament5",1);
            testUser9.setRandomPlayersOnTableInFirstTour("Tournament5",0);
            testUser9.setPointsOnTable("Tournament5",0,5);
            testUser9.finishTournament("Tournament5");

            testUser8.startOrganizeTournament("Tournament6", 3,2)
                    .with(testUser9)
                    .in(testAddress16)
                    .withGame( testGame1)
                    .startAt(format.parse("11-11-2018 10:13:00"))
                    .endingIn(format.parse("13-11-2018 10:13:00"))
                    .inviteParticipants(testUser5,testUser10,testUser1,testUser2)
                    .finishOrganize();

            Tournament testTournament6 = testUser9.startTournament("Tournament6",2);
            testUser9.setRandomPlayersOnTableInFirstTour("Tournament6",0);
            testUser9.setRandomPlayersOnTableInFirstTour("Tournament6",1);
            testUser9.setPointsOnTable("Tournament6",0,5);
            testUser9.setPointsOnTable("Tournament6",1,8);
            testUser9.prepareNextTour("Tournament6");
            testUser9.setPointsOnTable("Tournament6",0,8);
            testUser9.setPointsOnTable("Tournament6",1,10);
            testUser9.finishTournament("Tournament6");

            testUser10.startOrganizeTournament("Tournament7", 2,2)
                    .in(testAddress17)
                    .withGame( testGame1)
                    .startAt(format.parse("01-12-2018 11:06:00"))
                    .endingIn(format.parse("02-12-2018 11:06:00"))
                    .inviteParticipants(testUser10,testUser1,testUser3,testUser5)
                    .finishOrganize();

            Tournament testTournament7 = testUser10.startTournament("Tournament7",2);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament7",0);
            testUser10.setRandomPlayersOnTableInFirstTour("Tournament7",1);
            testUser10.setPointsOnTable("Tournament7",0,5);
            testUser10.setPointsOnTable("Tournament7",1,8);
            testUser10.prepareNextTour("Tournament7");
            testUser10.setPointsOnTable("Tournament7",0,8);
            testUser10.setPointsOnTable("Tournament7",1,10);
            testUser10.finishTournament("Tournament7");

            Tournament testTournament8 = testUser7
                    .startOrganizeTournament("Tournament8", 10,2)
                    .with(testUser8)
                    .in(testAddress18)
                    .withGame( testGame1)
                    .startAt(format.parse("02-06-2018 13:12:00"))
                    .endingIn(format.parse("03-06-2018 19:12:00"))
                    .inviteParticipants(testUser1,testUser2,testUser5,testUser6)
                    .finishOrganize();

            Tournament testTournament9 = testUser9
                    .startOrganizeTournament("Tournament9", 4,2)
                    .in(testAddress19)
                    .withGame( testGame1)
                    .startAt(format.parse("13-07-2018 08:17:00"))
                    .endingIn(format.parse("16-07-2018 14:17:00"))
                    .inviteParticipants(testUser10,testUser8)
                    .finishOrganize();

            Tournament testTournament10 = testUser9
                    .startOrganizeTournament("Tournament 10", 3,2)
                    .with(testUser8)
                    .in(testAddress20)
                    .withGame( testGame1)
                    .startAt(format.parse("26-08-2018 10:05:00"))
                    .endingIn(format.parse("26-08-2018 21:06:00"))
                    .inviteParticipants(testUser10,testUser7,testUser6,testUser2)
                    .finishOrganize();

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

            userAccountRepository.save(testUser11);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
