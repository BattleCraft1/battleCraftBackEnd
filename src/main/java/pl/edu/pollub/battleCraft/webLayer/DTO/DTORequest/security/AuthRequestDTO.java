package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.security;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    private String username;
    private String password;
}
