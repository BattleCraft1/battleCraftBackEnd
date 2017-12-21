package pl.edu.pollub.battleCraft.webLayer.controllers.singleEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.TournamentService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.DuelTournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.GroupTournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.TournamentToResponseDTOMapper;

@RestController
public class TournamentController {

    private final TournamentService tournamentService;

    private final TournamentToResponseDTOMapper tournamentToResponseDTOMapper;

    @Autowired
    public TournamentController(TournamentService tournamentService, TournamentToResponseDTOMapper tournamentToResponseDTOMapper){
        this.tournamentService = tournamentService;
        this.tournamentToResponseDTOMapper = tournamentToResponseDTOMapper;
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER')")
    @PostMapping(value = "/add/duel/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO addDuelTournament(@RequestBody DuelTournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.organizeTournament(tournamentRequestDTO, bindingResult));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/edit/duel/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO editDuelTournament(@RequestBody DuelTournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.editTournament(tournamentRequestDTO, bindingResult));
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER')")
    @PostMapping(value = "/add/group/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO addGroupTournament(@RequestBody GroupTournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.organizeTournament(tournamentRequestDTO, bindingResult));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/edit/group/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO editGroupTournament(@RequestBody GroupTournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.editTournament(tournamentRequestDTO, bindingResult));
    }

    @GetMapping(value = "/get/tournament")
    public TournamentResponseDTO getTournament(@RequestParam(value = "name") String name){
        return tournamentToResponseDTOMapper.map(tournamentService.getTournament(name));
    }
}
