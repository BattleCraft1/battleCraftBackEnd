package pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "vtoken")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerificationToken {

    public VerificationToken(String token, UserAccount user, int expiration) {
        super();
        this.token = token;
        this.user = user;
        user.setToken(this);
        this.date = calculateExpiryDate(expiration).getTime();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 70)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserAccount user;

    @NotNull
    private Long date;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void refreshDate(int expiration) {
        this.date=calculateExpiryDate(expiration).getTime();
    }
}
