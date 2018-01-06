package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRequestDTO extends AddressOwnerRequestDTO {
    protected String name;
    protected String nameChange;
    protected String email;
    protected String firstname;
    protected String lastname;
    protected String phoneNumber;
}
