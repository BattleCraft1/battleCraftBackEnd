package pl.edu.pollub.battleCraft.serviceLayer.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Admin.Administrator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.UserAccountService;

import java.util.Collections;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountService userAccountService;

    @Autowired
    public UserDetailsServiceImpl(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = this.userAccountService.getUserAccount(username);

        if (userAccount == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            boolean accountNonLocked;
            boolean enabled;
            if(userAccount instanceof Player){
                enabled = true;
                accountNonLocked = ((Player) userAccount).isBanned();
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
