package pl.edu.pollub.battleCraft.serviceLayer.services.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;

import java.util.List;

@Service
public class GameEnumsService {

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public GameEnumsService(AuthorityRecognizer authorityRecognizer) {

        this.authorityRecognizer = authorityRecognizer;
    }

    public List<String> getAllGamesNames() {
        return authorityRecognizer.getGamesNamesForCurrentUser();
    }
}
