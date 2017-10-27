package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces;

import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

public interface TournamentService{
    TournamentResponseDTO organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult);
    TournamentResponseDTO editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult);
    TournamentResponseDTO getTournament(String tournamentUniqueName);
}
