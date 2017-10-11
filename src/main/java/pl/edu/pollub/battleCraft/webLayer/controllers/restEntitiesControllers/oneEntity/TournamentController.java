package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.oneEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.implementations.TournamentServiceImpl;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament.TournamentWebDTO;

@RestController
public class TournamentController {
    private final TournamentServiceImpl tournamentService;

    @Autowired
    public TournamentController(TournamentServiceImpl tournamentService){
        this.tournamentService = tournamentService;
    }

    @PostMapping(value = "/organize/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addTournament(@RequestBody TournamentWebDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        tournamentService.organizeTournament(tournamentWebDTO, bindingResult);
    }

    @PostMapping(value = "/edit/tournament", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editTournament(@RequestBody TournamentWebDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {
        tournamentService.editTournament(tournamentWebDTO, bindingResult);
    }

    @GetMapping(value = "/get/tournament", produces = MediaType.APPLICATION_JSON_VALUE)
    public TournamentWebDTO getTournament(@RequestBody String tournamentUniqueName) throws EntityNotFoundException {
        return tournamentService.getTournament(tournamentUniqueName);
    }

    @PostMapping(value = "/delete/organizator")
    public void deleteOrganizatorFromTournament(@RequestBody String organizatorName) throws EntityNotFoundException{
        tournamentService.deleteOrganizatorFromTournament(organizatorName);
    }

    @PostMapping(value = "/delete/participant", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteParticipantFromTournament(@RequestBody String participantName) throws EntityNotFoundException{
        tournamentService.deleteParticipantFromTournament(participantName);
    }
}
