package pl.edu.pollub.battleCraft.webLayer.controllers.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.GroupTournamentManagementService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle.GroupBattleRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.GroupTournamentProgressResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentProgress.GroupTournamentProgressDTOMapper;

@RestController
public class GroupTournamentManagementController {

    private final GroupTournamentManagementService groupTournamentManagementService;

    private final GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper;

    @Autowired
    public GroupTournamentManagementController(GroupTournamentManagementService groupTournamentManagementService, GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper) {
        this.groupTournamentManagementService = groupTournamentManagementService;
        this.groupTournamentProgressDTOMapper = groupTournamentProgressDTOMapper;
    }

    @GetMapping(value ="/next/tour/group/tournament")
    public GroupTournamentProgressResponseDTO nextTour(@RequestParam(value = "name") String name){
        return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.nextTour(name));
    }

    @GetMapping(value ="/previous/tour/group/tournament")
    public GroupTournamentProgressResponseDTO previousTour(@RequestParam(value = "name") String name){
        return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.previousTour(name));
    }

    @PostMapping(value ="/set/points/group/tournament")
    public GroupTournamentProgressResponseDTO setPointsOnTable(@RequestParam(value = "name") String name, @RequestBody GroupBattleRequestDTO battleDTO){
        return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.setPoints(name, battleDTO));
    }

    @GetMapping(value ="/finish/group/tournament")
    public GroupTournamentProgressResponseDTO finishTournament(@RequestParam(value = "name") String name){
        return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.finishTournament(name));
    }
}
