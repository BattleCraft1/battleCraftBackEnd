package pl.edu.pollub.battleCraft.webLayer.controllers.raport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.report.ReportService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Report.ReportDTO;

@RestController
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService){
        this.reportService = reportService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER','ROLE_ADMIN','ROLE_ACCEPTED')")
    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendReportToAdmin(ReportDTO reportDTO){
        reportService.sendReportToAdmin(reportDTO);
    }
}
