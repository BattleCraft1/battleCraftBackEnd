package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.DuelTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.DuelTournamentManagementService;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.GroupTournamentManagementService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.TournamentProgressResponseDTO;

import java.util.Optional;

@RestController
public class TournamentProgressController {


    private final TournamentRepository tournamentRepository;

    private final DuelTournamentManagementService duelTournamentManagementService;

    private final GroupTournamentManagementService groupTournamentManagementService;

    @Autowired
    public TournamentProgressController(TournamentRepository tournamentRepository, DuelTournamentManagementService duelTournamentManagementService, GroupTournamentManagementService groupTournamentManagementService) {
        this.tournamentRepository = tournamentRepository;
        this.duelTournamentManagementService = duelTournamentManagementService;
        this.groupTournamentManagementService = groupTournamentManagementService;
    }

    @GetMapping(value ="/progress/tournament")
    public TournamentProgressResponseDTO getTournamentProgress(@RequestParam(value = "name") String name){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(name))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,name));
        if(tournament.getPlayersOnTableCount() == 2){
            return duelTournamentManagementService.getTournamentProgress((DuelTournament) tournament);
        }
        else{
            return groupTournamentManagementService.getTournamentProgress((GroupTournament) tournament);
        }
    }
}
