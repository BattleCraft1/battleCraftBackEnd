package pl.edu.pollub.battleCraft.data.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Participation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public Participation(UserAccount user, Tournament tournament) {
        this.user = user;
        this.tournament = tournament;
    }
}
