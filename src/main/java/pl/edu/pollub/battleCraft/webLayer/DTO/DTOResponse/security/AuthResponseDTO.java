package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String role;
}
