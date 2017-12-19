package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;

import java.util.Optional;

@Component
public class GameToResponseDTOMapper {

    private final AuthorityRecognizer authorityRecognizer;

    public GameToResponseDTOMapper(AuthorityRecognizer authorityRecognizer) {
        this.authorityRecognizer = authorityRecognizer;
    }

    public GameResponseDTO map(Game game){
        GameResponseDTO gameResponseDTO = new GameResponseDTO();
        gameResponseDTO.setCanCurrentUserEdit(authorityRecognizer.getCurrentUserRoleFromContext().equals("ROLE_ADMIN"));
        gameResponseDTO.setName( game.getName());
        gameResponseDTO.setNameChange(game.getName());
        if(game.getCreator() != null){
            gameResponseDTO.setCreatorName(game.getCreator().getName());
        }
        else{

            gameResponseDTO.setCreatorName("deleted");
        }
        gameResponseDTO.setDateOfCreation(game.getDateOfCreation());
        gameResponseDTO.setTournamentsNumber(game.getTournamentsNumber());

        if(game.isBanned())
            gameResponseDTO.setStatus("BANNED");
        else
            gameResponseDTO.setStatus(game.getStatus().name());
        return gameResponseDTO;
    }
}
