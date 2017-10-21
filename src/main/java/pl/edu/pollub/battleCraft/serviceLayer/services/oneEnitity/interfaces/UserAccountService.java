package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces;

import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

public interface UserAccountService {
    UserAccountResponseDTO editUserAccount(UserAccountRequestDTO tournamentWebDTO, BindingResult bindingResult);
    UserAccountResponseDTO getUserAccount(String name);
}
