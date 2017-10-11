package pl.edu.pollub.battleCraft.serviceLayer.services.enums.implementations;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.TournamentEnumsService;

import java.util.List;

@Service
public class TournamentEnumsServiceImpl implements TournamentEnumsService{

    @Override
    public List<String> getAllTournamentStatus() {
        return TournamentStatus.getNames();
    }

}
