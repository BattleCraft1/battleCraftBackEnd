package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.security;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private String username;
    private String role;
}
