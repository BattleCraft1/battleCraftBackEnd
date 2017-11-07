package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria;

import lombok.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SearchCriteria {
    private List<String> keys;
    private String operation;
    private List<Object> value;

    private Path getPath(Root root) {
        if(keys.size()==1){
            return root.get(keys.get(0));
        }
        else {
            Join path = root.join(keys.get(0));
            for (int i = 1; i < keys.size() - 1; i++)
                path = path.join(keys.get(i));
            return path.get(keys.get(keys.size()-1));
        }
    }

    private Class getType(Root root){
        return getPath(root).getJavaType();
    }

    public String getName() {
        if (keys.size() >= 2)
            return new StringBuilder(keys.get(keys.size() - 2)).append(".").append(keys.get(keys.size() - 1)).toString();
        else
            return keys.get(keys.size() - 1);
    }

    public List<Object> getValue(Root root){
        Class fieldType = getType(root);
        if (fieldType == Date.class) {
            return Collections.singletonList(convertValueToDate());
        } else if (fieldType == String.class) {
            return Collections.singletonList(convertValueToString());
        } else if (fieldType.getSuperclass() == Enum.class) {
            return value.stream()
                    .map(element -> convertValueToEnum(element,fieldType))
                    .collect(Collectors.toList());
        }
            return value;
    }

    private Date convertValueToDate(){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(value.get(0).toString());
        } catch (ParseException e) {
            return new Date();
        }
    }

    private String convertValueToString(){
        return new StringBuilder("%").append(value.get(0)).append("%").toString();
    }

    @SuppressWarnings("unchecked")
    private Enum convertValueToEnum(Object value,Class fieldType){
        return Enum.valueOf(fieldType, value.toString());
    }
}
