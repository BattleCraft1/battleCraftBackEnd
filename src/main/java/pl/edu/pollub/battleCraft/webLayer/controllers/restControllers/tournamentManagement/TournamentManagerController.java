package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.tournamentManagement;

import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.TournamentManagerService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentManagement.BattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentManagement.BattleRequestInFirstTourDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.TournamentProgressDTO;

@RestController
public class TournamentManagerController {

    private final TournamentManagerService tournamentManagerService;

    public TournamentManagerController(TournamentManagerService tournamentManagerService) {
        this.tournamentManagerService = tournamentManagerService;
    }

    @GetMapping(value ="/start/tournament")
    public TournamentProgressDTO startTournament(@RequestParam(value = "name") String name){
        tournamentManagerService.startTournament(name);
        return tournamentManagerService.getTournamentProgress(name);
    }

    @GetMapping(value ="/progress/tournament")
    public TournamentProgressDTO getTournamentProgress(@RequestParam(value = "name") String name){
        return tournamentManagerService.getTournamentProgress(name);
    }

    @GetMapping(value ="/next/tour")
    public void nextTour(@RequestParam(value = "name") String name){
        tournamentManagerService.nextTour(name);
    }

    @GetMapping(value ="/previous/tour")
    public void previousTour(@RequestParam(value = "name") String name){
        tournamentManagerService.previousTour(name);
    }

    @PostMapping(value ="/set/points")
    public void setPointsOnTable(@RequestParam(value = "name") String name, @RequestBody BattleRequestDTO battleDTO){
        tournamentManagerService.setPoints(battleDTO);
    }

    @PostMapping(value ="/set/points/firstTour")
    public void setPointsOnTableInFirstTour(@RequestParam(value = "name") String name, @RequestBody BattleRequestInFirstTourDTO battleDTO){
        tournamentManagerService.setPointsInFirstTour(battleDTO);
    }
}
