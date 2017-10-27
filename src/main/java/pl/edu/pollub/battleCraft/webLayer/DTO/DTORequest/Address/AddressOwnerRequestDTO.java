package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressOwnerRequestDTO {
    public String province;
    public String city;
    public String street;
    public String zipCode;
    public String description;
}
