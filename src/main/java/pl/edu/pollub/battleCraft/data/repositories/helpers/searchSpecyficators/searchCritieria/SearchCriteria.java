package pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.searchCritieria;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SearchCriteria {
    private List<String> keys;
    private String operation;
    private Object value;
}
