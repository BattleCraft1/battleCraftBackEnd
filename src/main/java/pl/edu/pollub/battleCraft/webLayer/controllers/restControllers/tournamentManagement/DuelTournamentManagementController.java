package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.DuelTournamentManagementService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Duel.Battle.DuelBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.DuelTournamentProgressResponseDTO;

@RestController
public class DuelTournamentManagementController {

    private final DuelTournamentManagementService duelTournamentManagementService;

    @Autowired
    public DuelTournamentManagementController(DuelTournamentManagementService duelTournamentManagementService) {
        this.duelTournamentManagementService = duelTournamentManagementService;
    }

    @GetMapping(value ="/start/duel/tournament")
    public DuelTournamentProgressResponseDTO startTournament(@RequestParam(value = "name") String name){
        return duelTournamentManagementService.startTournament(name);
    }

    @GetMapping(value ="/progress/duel/tournament")
    public DuelTournamentProgressResponseDTO getTournamentProgress(@RequestParam(value = "name") String name){
        return duelTournamentManagementService.getTournamentProgress(name);
    }

    @GetMapping(value ="/next/tour/duel/tournament")
    public DuelTournamentProgressResponseDTO nextTour(@RequestParam(value = "name") String name){
        return duelTournamentManagementService.nextTour(name);
    }

    @GetMapping(value ="/previous/tour/duel/tournament")
    public DuelTournamentProgressResponseDTO previousTour(@RequestParam(value = "name") String name){
        return duelTournamentManagementService.previousTour(name);
    }

    @PostMapping(value ="/set/points/duel/tournament")
    public DuelTournamentProgressResponseDTO setPointsOnTable(@RequestParam(value = "name") String name, @RequestBody DuelBattleRequestDTO battleDTO){
        return duelTournamentManagementService.setPoints(name, battleDTO);
    }

    @GetMapping(value ="/finish/duel/tournament")
    public DuelTournamentProgressResponseDTO finishTournament(@RequestParam(value = "name") String name){
        return duelTournamentManagementService.finishTournament(name);
    }
}
