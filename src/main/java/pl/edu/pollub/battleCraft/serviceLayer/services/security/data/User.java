package pl.edu.pollub.battleCraft.serviceLayer.services.security.data;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean enabled = true;

    public User(String username, String password, String email, boolean enabled, boolean accountNonLocked, UserType role) {
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setEnabled(enabled);
        this.setAccountNonLocked(accountNonLocked);
        this.setAuthorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_"+role.name())));
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
