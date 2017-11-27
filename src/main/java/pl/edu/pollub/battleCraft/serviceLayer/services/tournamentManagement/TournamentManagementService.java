package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.OrganizerRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.mappers.PlayerToOrganizerMapper;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentCannotStartYet;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentIsOutOfDate;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class TournamentManagementService {

    protected final TournamentRepository tournamentRepository;
    protected final AuthorityRecognizer authorityRecognizer;
    private final PlayerToOrganizerMapper playerToOrganizerMapper;
    private final OrganizerRepository organizerRepository;

    @Autowired
    protected TournamentManagementService(TournamentRepository tournamentRepository, AuthorityRecognizer authorityRecognizer, PlayerToOrganizerMapper playerToOrganizerMapper, OrganizerRepository organizerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.authorityRecognizer = authorityRecognizer;
        this.playerToOrganizerMapper = playerToOrganizerMapper;
        this.organizerRepository = organizerRepository;
    }

    public abstract Tournament startTournament(Tournament tournamentInput);
    public abstract Tournament nextTour(String name);
    public abstract Tournament previousTour(String name);

    Tournament findStartedTournamentByName(String tournamentName){
        return Optional.ofNullable(tournamentRepository.findStartedTournamentByUniqueName(tournamentName))
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentName));
    }

    void checkIfTournamentCanStart(Tournament tournament){
        if(tournament.getDateOfStart().before(new Date())){
            throw new TournamentCannotStartYet(tournament.getName(),tournament.getDateOfStart());
        }
    }

    void checkIfTournamentIsNotOutOfDate(Tournament tournament){
        if((new Date()).after(tournament.getDateOfEnd())){
            tournament.setBanned(true);
            tournamentRepository.save(tournament);
            throw new TournamentIsOutOfDate(tournament.getName(),tournament.getDateOfEnd());
        }
    }

    void advancePlayersToOrganizers(Tournament tournament){
        List<Player> playersToAdvance = tournament.getParticipation().stream()
                .map(Participation::getPlayer)
                .filter(player -> player.getStatus() == UserType.ACCEPTED && !(player instanceof Organizer))
                .filter(
                        player -> {
                            List<Tournament> finishedTournaments = player.getParticipation().stream()
                                    .map(Participation::getParticipatedTournament)
                                    .filter(tournament1 -> tournament1.getStatus() == TournamentStatus.FINISHED)
                                    .collect(Collectors.toList());
                            return finishedTournaments.size() == 15;
                        }
                ).collect(Collectors.toList());
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        organizerRepository.save(organizers);
    }

    private List<Organizer> advancePlayersToOrganizers(List<Player> players){
        List<Organizer> organizers = new ArrayList<>();
        players.forEach(
                player -> {
                    organizers.add(playerToOrganizerMapper.map(player));
                }
        );
        return organizers;
    }
}
