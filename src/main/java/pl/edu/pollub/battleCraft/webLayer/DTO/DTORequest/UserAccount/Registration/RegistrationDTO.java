package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Registration;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO extends UserAccountRequestDTO {
    private String password;
    private String passwordConfirm;
}
