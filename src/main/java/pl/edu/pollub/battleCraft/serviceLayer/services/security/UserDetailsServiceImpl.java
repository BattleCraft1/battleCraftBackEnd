package pl.edu.pollub.battleCraft.serviceLayer.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.TooManyLoginAttempts;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.data.User;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.UserAccountService;

import javax.servlet.http.HttpServletRequest;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountService userAccountService;

    private final LoginAttemptService loginAttemptService;


    @Autowired
    public UserDetailsServiceImpl(UserAccountService userAccountService, LoginAttemptService loginAttemptService) {
        this.userAccountService = userAccountService;
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
        UserAccount userAccount = this.userAccountService.getUserAccount(username);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String ip = getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            throw new TooManyLoginAttempts();
        }

        if (userAccount == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
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
                    null,
                    userAccount.getStatus());
        }
    }
}
