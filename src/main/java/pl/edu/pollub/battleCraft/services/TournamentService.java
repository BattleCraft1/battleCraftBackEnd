package pl.edu.pollub.battleCraft.services;

import pl.edu.pollub.battleCraft.entities.Tournament;

import javax.transaction.Transactional;
import java.util.List;

public interface TournamentService {
    @Transactional
    List<Tournament> getAllTournaments();
}
