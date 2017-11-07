package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field;


import lombok.*;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Field {
    private String name;
    private String value;
    private Function<String,Projection> operation;

    public Field(String name, String value){
        this.name = name;
        this.value = value;
        this.operation = Projections::property;
    }
}
