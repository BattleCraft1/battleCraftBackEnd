package pl.edu.pollub.battleCraft.dataLayer.initialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.builder.TournamentCreator;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.builder.UserCreator;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.builder.AdministratorBuilder;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.builder.OrganizerBuilder;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.builder.PlayerBuilder;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.GroupInvitationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationSender;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationToOrganizationSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TournamentRepository tournamentRepository;
    private final UserAccountRepository userAccountRepository;

    private final PlayerBuilder playerBuilder;
    private final OrganizerBuilder organizerBuilder;
    private final UserCreator userCreator;
    private final AdministratorBuilder administratorBuilder;

    private final GroupInvitationSender groupInvitationSender;
    private final InvitationSender invitationSender;
    private final InvitationToOrganizationSender invitationToOrganizationSender;

    private final TournamentCreator tournamentCreator;

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Autowired
    public DatabaseInitializer(TournamentRepository tournamentRepository, UserAccountRepository userAccountRepository, PlayerBuilder playerBuilder, OrganizerBuilder organizerBuilder, UserCreator userCreator, AdministratorBuilder administratorBuilder, GroupInvitationSender groupInvitationSender, InvitationSender invitationSender, InvitationToOrganizationSender invitationToOrganizationSender, TournamentCreator tournamentCreator) {
        this.userAccountRepository = userAccountRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerBuilder = playerBuilder;
        this.organizerBuilder = organizerBuilder;
        this.userCreator = userCreator;
        this.administratorBuilder = administratorBuilder;
        this.groupInvitationSender = groupInvitationSender;
        this.invitationSender = invitationSender;
        this.invitationToOrganizationSender = invitationToOrganizationSender;
        this.tournamentCreator = tournamentCreator;
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
        Address testAddress22 = new Address("opolskie", "Łódź", "Rozpaczy", "40-304","");

        Player testUser1 = playerBuilder
                .create("Bartek", "Nowogrodzki", "bart2123", "bart2123@gmail.com")
                .setPassword("bart21232123")
                .from(testAddress1)
                .withPhoneNumber("123123123").build();
        Player testUser2 = playerBuilder
                .create("Andrzej", "Bartoszewski", "art2123", "art2123@gmail.com")
                .setPassword("art2123")
                .from(testAddress2)
                .withPhoneNumber("123123123").build();
        Player testUser3 = playerBuilder
                .create("Artur", "Partoszewski", "part2123", "part2123@gmail.com")
                .setPassword("part2123")
                .from(testAddress3)
                .withPhoneNumber("123123123").build();
        Player testUser4 = playerBuilder
                .create("Jurek", "Jurkowski", "jur2123", "jur2123@gmail.com")
                .setPassword("jur2123")
                .from(testAddress4)
                .withPhoneNumber("123123123").build();
        Player testUser5 = playerBuilder
                .create("Anna", "Bielec", "biel2123", "biel2123@gmail.com")
                .setPassword("biel21232123")
                .from(testAddress5).build();
        Player testUser6 = playerBuilder
                .create("Tomasz", "Blawucki", "blaw2123", "blawl2123@gmail.com")
                .setPassword("blaw2123")
                .from(testAddress6).build();
        Organizer testUser7 = organizerBuilder
                .create("Filip", "Begiello", "beg2123", "beg2123@gmail.com")
                .setPassword("beg21232123")
                .from(testAddress7)
                .withPhoneNumber("123123123").build();
        Organizer testUser8 = organizerBuilder
                .create("Lukasz", "Depta", "dept2123", "dept2123@gmail.com")
                .setPassword("dept21232123")
                .from(testAddress8)
                .withPhoneNumber("123123123").build();
        Organizer testUser9 = organizerBuilder
                .create("Albert", "Kwasny", "kwas2123", "kwas2123@gmail.com")
                .setPassword("kwas2123")
                .from(testAddress9)
                .withPhoneNumber("123123123").build();
        Organizer testUser10 = organizerBuilder
                .create("Anastazja", "Nijaka", "nijak2123", "nijak2123@gmail.com")
                .setPassword("nijak2123")
                .from(testAddress10)
                .withPhoneNumber("123123123").build();

        UserAccount testUser11 = userCreator
                .create("Pawel", "Maziarczuk", "mazi2123", "mazi2123@gmail.com")
                .setPassword("mazi2123")
                .from(testAddress21)
                .withPhoneNumber("123123123").build();

        Administrator testAdmin1 = administratorBuilder
                .create("Admin", "Admin", "admin", "6darksavant9@gmail.com")
                .setPassword("admin2123")
                .from(testAddress22)
                .withPhoneNumber("123123123").build();

        Game testGame1 = new Game("Warhammer",testUser10);
        testGame1.setStatus(GameStatus.ACCEPTED);

        try {
            Tournament testTournament1 = tournamentCreator
                    .startOrganizeTournament("Tournament1", 2, TournamentType.GROUP,4)
                    .in(testAddress11)
                    .withGame( testGame1)
                    .startAt(format.parse("08-01-2018 13:05:00"))
                    .endingIn(format.parse("09-01-2018 14:05:00"))
                    .finishOrganize();
            testTournament1.setStatus(TournamentStatus.ACCEPTED);

            invitationToOrganizationSender.inviteOrganizersList(testTournament1,Arrays.asList(testUser8,testUser9));

            groupInvitationSender.inviteParticipantsGroupsList(
                    testTournament1,
                    Arrays.asList(
                            Arrays.asList(testUser1,testUser2),
                            Arrays.asList(testUser3,testUser4),
                            Arrays.asList(testUser5,testUser6))
            );

            Tournament testTournament2 = tournamentCreator
                    .startOrganizeTournament("Tournament2", 4,TournamentType.DUEL,4)
                    .in(testAddress12)
                    .withGame( testGame1)
                    .startAt(format.parse("09-02-2018 14:11:00"))
                    .endingIn(format.parse("11-02-2018 14:05:00"))
                    .finishOrganize();
            testTournament2.setStatus(TournamentStatus.ACCEPTED);

            invitationToOrganizationSender.inviteOrganizersList(testTournament2,Collections.singletonList(testUser9));

            invitationSender.inviteParticipantsList(testTournament2,Arrays.asList(testUser2,testUser3,testUser5,testUser9,testUser1));

            Tournament testTournament3 = tournamentCreator
                    .startOrganizeTournament("Tournament3", 3,TournamentType.DUEL,2)
                    .in(testAddress13)
                    .withGame( testGame1)
                    .startAt(format.parse("12-03-2018 15:15:00"))
                    .endingIn(format.parse("12-03-2018 16:05:00"))
                    .finishOrganize();

            invitationToOrganizationSender.inviteOrganizersList(testTournament3,Collections.singletonList(testUser9));

            invitationSender.inviteParticipantsList(testTournament3,Arrays.asList(testUser1,testUser3,testUser5,testUser7));


            Tournament testTournament4 = tournamentCreator
                    .startOrganizeTournament("Tournament4",  5,TournamentType.DUEL,2)
                    .in(testAddress14)
                    .withGame(testGame1)
                    .startAt(format.parse("25-04-2018 16:25:00"))
                    .endingIn(format.parse("27-04-2018 16:05:00"))
                    .finishOrganize();

            invitationToOrganizationSender.inviteOrganizersList(testTournament4,Collections.singletonList(testUser7));

            invitationSender.inviteParticipantsList(testTournament4,Arrays.asList(testUser2,testUser4,testUser6,testUser8));

            Tournament testTournament5 = tournamentCreator
                    .startOrganizeTournament("Tournament5", 4,TournamentType.DUEL,1)
                    .in(testAddress15)
                    .withGame( testGame1)
                    .startAt(format.parse("13-05-2018 11:24:00"))
                    .endingIn(format.parse("15-05-2018 16:05:00"))
                    .finishOrganize();

            invitationToOrganizationSender.inviteOrganizersList(testTournament5,Collections.singletonList(testUser7));

            invitationSender.inviteParticipantsList(testTournament5,Arrays.asList(testUser5,testUser10));

            Tournament testTournament6 = tournamentCreator
                    .startOrganizeTournament("Tournament6", 3,TournamentType.DUEL,2)
                    .in(testAddress16)
                    .withGame( testGame1)
                    .startAt(format.parse("11-11-2018 10:13:00"))
                    .endingIn(format.parse("13-11-2018 10:13:00"))
                    .finishOrganize();

            invitationToOrganizationSender.inviteOrganizersList(testTournament6,Collections.singletonList(testUser9));

            invitationSender.inviteParticipantsList(testTournament6,Arrays.asList(testUser5,testUser10,testUser1,testUser2));

            Tournament testTournament7 = tournamentCreator
                    .startOrganizeTournament("Tournament7", 2,TournamentType.DUEL,2)
                    .in(testAddress17)
                    .withGame( testGame1)
                    .startAt(format.parse("01-12-2018 11:06:00"))
                    .endingIn(format.parse("02-12-2018 11:06:00"))
                    .finishOrganize();

            invitationToOrganizationSender.inviteOrganizersList(testTournament7,Collections.singletonList(testUser8));

            invitationSender.inviteParticipantsList(testTournament7,Arrays.asList(testUser10,testUser1,testUser3,testUser5));

            Tournament testTournament8 = tournamentCreator
                    .startOrganizeTournament("Tournament8", 10,TournamentType.DUEL,2)
                    .in(testAddress18)
                    .withGame( testGame1)
                    .startAt(format.parse("02-06-2018 13:12:00"))
                    .endingIn(format.parse("03-06-2018 19:12:00"))
                    .finishOrganize();
            testTournament8.setStatus(TournamentStatus.ACCEPTED);

            invitationToOrganizationSender.inviteOrganizersList(testTournament8,Collections.singletonList(testUser8));

            invitationSender.inviteParticipantsList(testTournament8,Arrays.asList(testUser1,testUser2,testUser5,testUser6));

            Tournament testTournament9 = tournamentCreator
                    .startOrganizeTournament("Tournament9", 4,TournamentType.DUEL,2)
                    .in(testAddress19)
                    .withGame( testGame1)
                    .startAt(format.parse("13-07-2018 08:17:00"))
                    .endingIn(format.parse("16-07-2018 14:17:00"))
                    .finishOrganize();
            testTournament9.setStatus(TournamentStatus.ACCEPTED);

            invitationToOrganizationSender.inviteOrganizersList(testTournament9,Collections.singletonList(testUser7));

            invitationSender.inviteParticipantsList(testTournament9,Arrays.asList(testUser10,testUser8));

            Tournament testTournament10 = tournamentCreator
                    .startOrganizeTournament("Tournament 10", 3,TournamentType.DUEL,2)
                    .in(testAddress20)
                    .withGame( testGame1)
                    .startAt(format.parse("26-08-2018 10:05:00"))
                    .endingIn(format.parse("26-08-2018 21:06:00"))
                    .finishOrganize();
            testTournament10.setStatus(TournamentStatus.ACCEPTED);

            invitationToOrganizationSender.inviteOrganizersList(testTournament10,Collections.singletonList(testUser8));

            invitationSender.inviteParticipantsList(testTournament10,Arrays.asList(testUser10,testUser7,testUser6,testUser2));

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
            userAccountRepository.save(testAdmin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
