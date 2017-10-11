package pl.edu.pollub.battleCraft.serviceLayer.services.enums.implementations;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.UserAccountEnumsService;

import java.util.List;

@Service
public class UserAccountEnumsServiceImpl implements UserAccountEnumsService{

    @Override
    public List<String> getAllUserTypes() {
        return UserType.getNames();
    }
}
