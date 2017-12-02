package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Security;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String oldPassword;
    private String password;
    private String passwordConfirm;
}
