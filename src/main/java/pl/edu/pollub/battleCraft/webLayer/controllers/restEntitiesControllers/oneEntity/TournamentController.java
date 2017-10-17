package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.oneEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations.TournamentServiceImpl;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

@RestController
public class TournamentController {

    private final TournamentServiceImpl tournamentService;

    @Autowired
    public TournamentController(TournamentServiceImpl tournamentService){
        this.tournamentService = tournamentService;
    }

    @PostMapping(value = "/add/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO addTournament(@RequestBody TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        return tournamentService.organizeTournament(tournamentWebDTO, bindingResult);
    }

    @PostMapping(value = "/edit/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TournamentResponseDTO editTournament(@RequestBody TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        return tournamentService.editTournament(tournamentWebDTO, bindingResult);
    }

    @GetMapping(value = "/get/tournament")
    public TournamentResponseDTO getTournament(@RequestParam(value = "name") String name) throws EntityNotFoundException {
        return tournamentService.getTournament(name);
    }
}
