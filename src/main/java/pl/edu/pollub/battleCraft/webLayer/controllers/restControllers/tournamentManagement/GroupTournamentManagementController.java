package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.GroupTournamentManagementService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.GroupTournamentProgressResponseDTO;

@RestController
public class GroupTournamentManagementController {

    private final GroupTournamentManagementService groupTournamentManagementService;

    @Autowired
    public GroupTournamentManagementController(GroupTournamentManagementService groupTournamentManagementService) {
        this.groupTournamentManagementService = groupTournamentManagementService;
    }

    @GetMapping(value ="/start/group/tournament")
    public GroupTournamentProgressResponseDTO startTournament(@RequestParam(value = "name") String name){
        return groupTournamentManagementService.startTournament(name);
    }

    @GetMapping(value ="/progress/group/tournament")
    public GroupTournamentProgressResponseDTO getTournamentProgress(@RequestParam(value = "name") String name){
        return groupTournamentManagementService.getTournamentProgress(name);
    }

    @GetMapping(value ="/next/tour/group/tournament")
    public GroupTournamentProgressResponseDTO nextTour(@RequestParam(value = "name") String name){
        return groupTournamentManagementService.nextTour(name);
    }

    @GetMapping(value ="/previous/tour/group/tournament")
    public GroupTournamentProgressResponseDTO previousTour(@RequestParam(value = "name") String name){
        return groupTournamentManagementService.previousTour(name);
    }

    @PostMapping(value ="/set/points/group/tournament")
    public GroupTournamentProgressResponseDTO setPointsOnTable(@RequestParam(value = "name") String name, @RequestBody GroupBattleRequestDTO battleDTO){
        return groupTournamentManagementService.setPoints(name, battleDTO);
    }

    @GetMapping(value ="/finish/group/tournament")
    public GroupTournamentProgressResponseDTO finishTournament(@RequestParam(value = "name") String name){
        return groupTournamentManagementService.finishTournament(name);
    }
}
