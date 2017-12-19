package pl.edu.pollub.battleCraft.serviceLayer.services.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;

import java.util.Date;
import java.util.List;

@Service
public class TournamentScheduler {

    private final TournamentRepository tournamentRepository;

    public TournamentScheduler(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    public void banOutOfDateTournaments(){
        List<Tournament> newOrAcceptedTournaments = tournamentRepository.findAllAcceptedOrNewTournaments();
        newOrAcceptedTournaments.forEach(tournament -> {
            if(tournament.getDateOfStart().after(new Date()) || tournament.getDateOfEnd().after(new Date())){
                tournament.setBanned(true);
                tournamentRepository.save(tournament);
            }
        });

        List<Tournament> startedTournaments = tournamentRepository.findAllStartedTournament();
        startedTournaments.forEach(tournament -> {
            if(tournament.getDateOfEnd().after(new Date())){
                tournament.setBanned(true);
                tournamentRepository.save(tournament);
            }
        });
    }
}
