package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentCannotStartYet;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentIsOutOfDate;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class TournamentManagementService {

    @PersistenceContext
    private EntityManager entityManager;
    protected final TournamentRepository tournamentRepository;
    protected final AuthorityRecognizer authorityRecognizer;

    @Autowired
    protected TournamentManagementService(TournamentRepository tournamentRepository, AuthorityRecognizer authorityRecognizer) {
        this.tournamentRepository = tournamentRepository;
        this.authorityRecognizer = authorityRecognizer;
    }

    public abstract Tournament startTournament(Tournament tournamentInput);
    public abstract Tournament nextTurn(String name);
    public abstract Tournament previousTurn(String name);

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

        List<String> playersToAdvanceNames = tournament.getParticipation().stream()
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
                )
                .map(UserAccount::getName)
                .collect(Collectors.toList());

        entityManager.createNativeQuery("UPDATE user_account SET status = 'ORGANIZER', role = 'Organizer' WHERE name in (:uniqueNames)")
                .setParameter("uniqueNames",playersToAdvanceNames).executeUpdate();
    }
}
