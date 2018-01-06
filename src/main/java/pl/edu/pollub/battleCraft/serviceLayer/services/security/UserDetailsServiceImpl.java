package pl.edu.pollub.battleCraft.serviceLayer.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.MyAccessDeniedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.TooManyLoginAttempts;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.data.User;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.UserAccountService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    private final LoginAttemptService loginAttemptService;


    @Autowired
    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository, LoginAttemptService loginAttemptService) {
        this.userAccountRepository = userAccountRepository;
        this.loginAttemptService = loginAttemptService;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount;
        userAccount = Optional.ofNullable(userAccountRepository.findUserAccountByUniqueName(username))
                .orElseThrow(() -> new ObjectNotFoundException(UserAccount.class,username));

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String ip = getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            throw new TooManyLoginAttempts();
        }

        boolean accountNonLocked;
        boolean enabled;
        if(userAccount instanceof Player && !userAccount.getStatus().equalsName("NEW")){
            enabled = true;
            accountNonLocked = !((Player) userAccount).isBanned();
        }
        else if(userAccount instanceof Administrator){
            enabled = true;
            accountNonLocked = true;
        }
        else{
            enabled = false;
            accountNonLocked = true;
        }

        return new User(
                userAccount.getName(),
                userAccount.getPassword(),
                userAccount.getEmail(),
                enabled,
                accountNonLocked,
                userAccount.getStatus());
    }
}
