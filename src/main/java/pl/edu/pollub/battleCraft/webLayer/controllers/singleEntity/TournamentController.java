package pl.edu.pollub.battleCraft.webLayer.controllers.singleEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.TournamentService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/add/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO addTournament(@RequestBody TournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.organizeTournament(tournamentRequestDTO, bindingResult));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/edit/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO editTournament(@RequestBody TournamentRequestDTO tournamentRequestDTO, BindingResult bindingResult){
        return tournamentToResponseDTOMapper.map(tournamentService.editTournament(tournamentRequestDTO, bindingResult));
    }

    @GetMapping(value = "/get/tournament")
    public TournamentResponseDTO getTournament(@RequestParam(value = "name") String name){
        return tournamentToResponseDTOMapper.map(tournamentService.getTournament(name));
    }
}
