package pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators;

import lombok.*;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Path getPath(Root root) {
        Path path = root.get(keys.get(0));
        for (int i = 1; i < keys.size(); i++)
            path = path.get(keys.get(i));
        return path;
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

    public Object getValue(Root root){
        Class fieldType = getType(root);
        if (fieldType == Date.class) {
            return convertValueToDate();
        } else if (fieldType == String.class) {
            return convertValueToString();
        } else if (fieldType.getSuperclass() == Enum.class) {
            return convertValueToEnum(fieldType);
        }
            return value;
    }

    private Date convertValueToDate(){
        try {
            return format.parse(value.toString());
        } catch (ParseException e) {
            return new Date();
        }
    }

    private String convertValueToString(){
        return new StringBuilder("%").append(value).append("%").toString();
    }

    private Enum convertValueToEnum(Class fieldType){
        return Enum.valueOf(fieldType, value.toString());
    }
}
