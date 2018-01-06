package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressOwnerRequestDTO {
    private String province;
    private String city;
    private String street;
    private String zipCode;
    private String description;
}
