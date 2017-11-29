package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Report;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    List<String> objectNames;
    String objectType;
    String reportMessage;
}
