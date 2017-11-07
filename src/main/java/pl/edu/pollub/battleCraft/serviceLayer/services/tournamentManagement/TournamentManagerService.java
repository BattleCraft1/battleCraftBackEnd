package pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentManagement.BattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentManagement.BattleRequestInFirstTourDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.TournamentProgressDTO;

@Service
public class TournamentManagerService{

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentManagerService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public void startTournament(String name) {
        Tournament tournament = tournamentRepository.findAcceptedTournamentByUniqueName(name);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);

        tournament.filterNoAcceptedParticipation();

        tournament.filterNoAcceptedOrganizations();

        for(int toursNumber=0;toursNumber<tournament.getToursCount();toursNumber++){
            tournament.getTours().add(new Tour(toursNumber,tournament));
        }

        tournament.setCurrentTourNumber(0);
    }

    public void setPoints(BattleRequestDTO battleDTO) {
    }

    public void setPointsInFirstTour(BattleRequestInFirstTourDTO battleDTO) {
    }

    public TournamentProgressDTO getTournamentProgress(String name) {
        return null;
    }

    public void nextTour(String name) {
    }

    public void previousTour(String name) {
    }

}
