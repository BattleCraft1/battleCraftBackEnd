package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Report;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    String objectName;
    String objectType;
    String reportMessage;
}
