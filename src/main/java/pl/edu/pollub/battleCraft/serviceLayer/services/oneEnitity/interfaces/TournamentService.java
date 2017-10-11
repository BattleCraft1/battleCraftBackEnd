package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces;

import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Tournament.TournamentWebDTO;

public interface TournamentService{
    void organizeTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult);
    void editTournament(TournamentWebDTO tournamentWebDTO, BindingResult bindingResult);
    TournamentWebDTO getTournament(String tournamentUniqueName);
    void deleteOrganizatorFromTournament(String organizatorName);
    void deleteParticipantFromTournament(String participantName);
}
