package pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria;

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
    private String type;
    private Object value;
}
