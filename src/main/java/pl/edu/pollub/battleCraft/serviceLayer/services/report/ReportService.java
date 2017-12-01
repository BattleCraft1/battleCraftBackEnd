package pl.edu.pollub.battleCraft.serviceLayer.services.report;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.AdminRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Report.ReportDTO;

import java.util.List;

@Service
public class ReportService {

    private final MailUtil mailUtil;

    private final AdminRepository adminRepository;

    private final AuthorityRecognizer authorityRecognizer;

    public ReportService(MailUtil mailUtil, AdminRepository adminRepository, AuthorityRecognizer authorityRecognizer) {
        this.mailUtil = mailUtil;
        this.adminRepository = adminRepository;
        this.authorityRecognizer = authorityRecognizer;
    }

    public void sendReportToAdmin(ReportDTO reportDTO){
        List<Administrator> administratorList = adminRepository.findAll();
        String userWhoReportName = authorityRecognizer.getCurrentUserNameFromContext();
        mailUtil.sendReportToAdmin(userWhoReportName,reportDTO,administratorList.get(0).getEmail());

    }
}
