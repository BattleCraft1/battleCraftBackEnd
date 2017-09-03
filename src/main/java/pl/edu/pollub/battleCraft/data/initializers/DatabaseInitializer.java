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
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.UserBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.OrganizerBuilder;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.PlayerBuilder;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.ProvinceRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.UserAccountRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TournamentRepository tournamentRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProvinceRepository provinceRepository;

    private final TournamentBuilder tournamentBuilder = new TournamentBuilder();
    private final PlayerBuilder playerBuilder = new PlayerBuilder();
    private final OrganizerBuilder organizerBuilder = new OrganizerBuilder();
    private final UserBuilder userBuilder = new UserBuilder();

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @Autowired
    public DatabaseInitializer(TournamentRepository tournamentRepository, UserAccountRepository userAccountRepository, ProvinceRepository provinceRepository) {
        this.userAccountRepository = userAccountRepository;
        this.provinceRepository = provinceRepository;
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        tournamentRepository.deleteAll();
        userAccountRepository.deleteAll();
        provinceRepository.deleteAll();

        Province testProvince1 = new Province("lubelskie");
        Province testProvince2 = new Province("dolnośląskie");
        Province testProvince3 = new Province("małopolskie");
        Province testProvince4 = new Province("śląskie");
        Province testProvince5 = new Province("zachodiopomorskie");
        Province testProvince6 = new Province("wielkopolskie");
        Province testProvince7 = new Province("opolskie");
        Province testProvince8 = new Province("łódzkie");
        Province testProvince9 = new Province("podlaskie");

        provinceRepository.save(testProvince1);
        provinceRepository.save(testProvince2);
        provinceRepository.save(testProvince3);
        provinceRepository.save(testProvince4);
        provinceRepository.save(testProvince5);
        provinceRepository.save(testProvince6);
        provinceRepository.save(testProvince7);
        provinceRepository.save(testProvince8);
        provinceRepository.save(testProvince9);

        Address testAddress1 = new Address(testProvince1, "Lublin", "Nadbystrzycka", "20-501");
        Address testAddress2 = new Address(testProvince1, "Zamość", "1 Maja", "30-301");
        Address testAddress3 = new Address(testProvince2, "Wrocław", "Krańcowa", "40-301");
        Address testAddress4 = new Address(testProvince3, "Kraków", "Ostatnia", "50-301");
        Address testAddress5 = new Address(testProvince4, "Katowice", "Szara", "60-301");
        Address testAddress6 = new Address(testProvince5, "Szczecin", "Nudna", "70-301");
        Address testAddress7 = new Address(testProvince6, "Poznań", "Smutna", "80-301");
        Address testAddress8 = new Address(testProvince7, "Opole", "Długa", "32-302");
        Address testAddress9 = new Address(testProvince8, "Łódź", "Czarna", "33-303");
        Address testAddress10 = new Address(testProvince9, "Białystok", "Rozpaczy", "40-304");
        Address testAddress11 = new Address(testProvince1, "Lublin", "Nadbystrzycka", "20-501");
        Address testAddress12 = new Address(testProvince1, "Zamość", "1 Maja", "30-301");
        Address testAddress13 = new Address(testProvince2, "Wrocław", "Krańcowa", "40-301");
        Address testAddress14 = new Address(testProvince3, "Kraków", "Ostatnia", "50-301");
        Address testAddress15 = new Address(testProvince4, "Katowice", "Szara", "60-301");
        Address testAddress16 = new Address(testProvince5, "Szczecin", "Nudna", "70-301");
        Address testAddress17 = new Address(testProvince6, "Poznań", "Smutna", "80-301");
        Address testAddress18 = new Address(testProvince7, "Opole", "Długa", "32-302");
        Address testAddress19 = new Address(testProvince8, "Łódź", "Czarna", "33-303");
        Address testAddress20 = new Address(testProvince9, "Białystok", "Rozpaczy", "40-304");
        Address testAddress21 = new Address(testProvince9, "Łódź", "Rozpaczy", "40-304");

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


        Game testGame1 = new Game("Warhammer");
        Game testGame2 = new Game("Star wars");
        Game testGame3 = new Game("Warhammer 40k");
        Game testGame4 = new Game("Cyber punk");
        Game testGame5 = new Game("Heroes");
        Game testGame6 = new Game("Lord of the rings");

        try {
            Tournament testTournament1 = tournamentBuilder
                    .create("Tournament1", 6, 3, TournamentClass.CHALLENGER)
                    .in(testAddress11)
                    .withGame( testGame1)
                    .startAt(format.parse("08-01-2017 13:05:00"))
                    .endingIn(format.parse("09-01-2017 14:05:00"))
                    .organizedBy(testUser7,testUser8)
                    .withParticipants(testUser1,testUser2,testUser3,testUser4)
                    .build();

            Tournament testTournament2 = tournamentBuilder
                    .create("Tournament2", 8, 4, TournamentClass.LOCAL)
                    .in(testAddress12)
                    .withGame( testGame2)
                    .startAt(format.parse("09-02-2018 14:11:00"))
                    .endingIn(format.parse("11-02-2017 14:05:00"))
                    .organizedBy(testUser10)
                    .withParticipants(testUser2,testUser3,testUser5,testUser9)
                    .build();

            Tournament testTournament3 = tournamentBuilder
                    .create("Tournament3", 6, 3, TournamentClass.MASTER)
                    .in(testAddress13)
                    .withGame( testGame3)
                    .startAt(format.parse("12-03-2017 15:15:00"))
                    .endingIn(format.parse("12-03-2017 16:05:00"))
                    .organizedBy(testUser7,testUser9)
                    .withParticipants(testUser1,testUser3,testUser5,testUser7)
                    .build();

            Tournament testTournament4 = tournamentBuilder
                    .create("Tournament4", 10, 5, TournamentClass.CHALLENGER)
                    .in(testAddress14)
                    .withGame( testGame4)
                    .startAt(format.parse("25-04-2018 16:25:00"))
                    .endingIn(format.parse("27-04-2018 16:05:00"))
                    .organizedBy(testUser10)
                    .withParticipants(testUser2,testUser4,testUser6,testUser8)
                    .build();


            Tournament testTournament5 = tournamentBuilder
                    .create("Tournament5", 8, 4, TournamentClass.CHALLENGER)
                    .in(testAddress15)
                    .withGame( testGame5)
                    .startAt(format.parse("13-05-2017 11:24:00"))
                    .endingIn(format.parse("15-05-2018 16:05:00"))
                    .organizedBy(testUser9,testUser7)
                    .withParticipants(testUser5,testUser10)
                    .build();

            Tournament testTournament6 = tournamentBuilder
                    .create("Tournament6", 6, 3, TournamentClass.MASTER)
                    .in(testAddress16)
                    .withGame( testGame6)
                    .startAt(format.parse("11-11-2018 10:13:00"))
                    .endingIn(format.parse("15-11-2018 10:13:00"))
                    .organizedBy(testUser8,testUser9)
                    .withParticipants(testUser5,testUser10,testUser1,testUser2)
                    .build();

            Tournament testTournament7 = tournamentBuilder
                    .create("Tournament7", 4, 2, TournamentClass.CHALLENGER)
                    .in(testAddress17)
                    .withGame( testGame1)
                    .startAt(format.parse("01-12-2017 11:06:00"))
                    .endingIn(format.parse("02-12-2017 11:06:00"))
                    .organizedBy(testUser10)
                    .withParticipants(testUser10,testUser1,testUser3,testUser5)
                    .build();

            Tournament testTournament8 = tournamentBuilder
                    .create("Tournament8", 20, 10, TournamentClass.LOCAL)
                    .in(testAddress18)
                    .withGame( testGame2)
                    .startAt(format.parse("02-06-2018 12:12:00"))
                    .endingIn(format.parse("03-06-2018 12:12:00"))
                    .organizedBy(testUser7,testUser8)
                    .withParticipants(testUser1,testUser2,testUser5,testUser6)
                    .build();

            Tournament testTournament9 = tournamentBuilder
                    .create("Tournament9", 8, 4, TournamentClass.MASTER)
                    .in(testAddress19)
                    .withGame( testGame3)
                    .startAt(format.parse("13-07-2017 17:17:00"))
                    .endingIn(format.parse("16-07-2017 17:17:00"))
                    .organizedBy(testUser9)
                    .withParticipants(testUser10,testUser8)
                    .build();

            Tournament testTournament10 = tournamentBuilder
                    .create("Tournament10", 6, 3, TournamentClass.LOCAL)
                    .in(testAddress20)
                    .withGame( testGame4)
                    .startAt(format.parse("26-08-2018 18:05:00"))
                    .endingIn(format.parse("26-08-2018 18:05:00"))
                    .organizedBy(testUser9,testUser8)
                    .withParticipants(testUser10,testUser7,testUser6,testUser2)
                    .build();

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
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
