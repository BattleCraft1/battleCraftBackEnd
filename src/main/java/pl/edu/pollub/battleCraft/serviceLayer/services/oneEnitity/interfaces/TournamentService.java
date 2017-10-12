package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces;

import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament.TournamentWebDTO;

public interface TournamentService{
    TournamentWebDTO organizeTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult);
    TournamentWebDTO editTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult);
    TournamentWebDTO getTournament(String tournamentUniqueName);
}
