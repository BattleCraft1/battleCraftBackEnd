package pl.edu.pollub.battleCraft.webLayer.controllers.tournamentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsNotAcceptedException;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.DuelTournamentManagementService;
import pl.edu.pollub.battleCraft.serviceLayer.services.tournamentManagement.GroupTournamentManagementService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.TournamentProgressResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentProgress.DuelTournamentProgressDTOMapper;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentProgress.GroupTournamentProgressDTOMapper;

import java.util.Optional;

@RestController
public class TournamentProgressController {


    private final TournamentRepository tournamentRepository;

    private final DuelTournamentManagementService duelTournamentManagementService;

    private final GroupTournamentManagementService groupTournamentManagementService;

    private final DuelTournamentProgressDTOMapper duelTournamentProgressDTOMapper;

    private final GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper;

    @Autowired
    public TournamentProgressController(TournamentRepository tournamentRepository, DuelTournamentManagementService duelTournamentManagementService, GroupTournamentManagementService groupTournamentManagementService, DuelTournamentProgressDTOMapper duelTournamentProgressDTOMapper, GroupTournamentProgressDTOMapper groupTournamentProgressDTOMapper) {
        this.tournamentRepository = tournamentRepository;
        this.duelTournamentManagementService = duelTournamentManagementService;
        this.groupTournamentManagementService = groupTournamentManagementService;
        this.duelTournamentProgressDTOMapper = duelTournamentProgressDTOMapper;
        this.groupTournamentProgressDTOMapper = groupTournamentProgressDTOMapper;
    }

    @GetMapping(value ="/progress/tournament")
    public TournamentProgressResponseDTO getTournamentProgress(@RequestParam(value = "name") String name){
        Tournament tournament = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(name))
                .orElseThrow(() -> new ObjectNotFoundException(Tournament.class,name));


        if(tournament.isBanned()){
            throw new ThisObjectIsBannedException(Tournament.class,tournament.getName());
        }
        if(tournament.getStatus()==TournamentStatus.FINISHED || tournament.getStatus()==TournamentStatus.IN_PROGRESS){
            if(tournament.getPlayersOnTableCount() == 2){
                return duelTournamentProgressDTOMapper.map(duelTournamentManagementService.castToDuelTournament(tournament));
            }
            else{
                return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.castToGroupTournament(tournament));
            }
        }
        if(tournament.getStatus() == TournamentStatus.ACCEPTED){
            if(tournament.getPlayersOnTableCount() == 2){
                return duelTournamentProgressDTOMapper.map(duelTournamentManagementService.startTournament(tournament));
            }
            else{
                return groupTournamentProgressDTOMapper.map(groupTournamentManagementService.startTournament(tournament));
            }
        }


        throw new ThisObjectIsNotAcceptedException(Tournament.class,tournament.getName());
    }
}
