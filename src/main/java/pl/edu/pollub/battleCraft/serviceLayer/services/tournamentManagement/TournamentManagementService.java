package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentCannotStartYet;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentIsOutOfDate;

import java.util.Date;
import java.util.Optional;

public abstract class TournamentManagementService {

    protected final TournamentRepository tournamentRepository;

    @Autowired
    protected TournamentManagementService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
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
}
