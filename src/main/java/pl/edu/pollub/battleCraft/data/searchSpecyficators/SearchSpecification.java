package pl.edu.pollub.battleCraft.data.searchSpecyficators;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.jpa.domain.Specification;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchSpecification<V> implements Specification<V>{

    public SearchSpecification() {
    }

    public SearchSpecification(List<SearchCriteria> criteries) {
        this.criteria = criteries;
    }

    private List<SearchCriteria> criteria;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public List<SearchCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<SearchCriteria> criteria) {
        this.criteria = criteria;
    }

    public void toRestrictions(Criteria restrictions, Root<V> rootEntity){
        criteria.forEach((criteria) -> {
                Object fieldValue = criteria.getValue();
                Path<Object> fieldPath = this.getFieldPathByKeys(criteria.getKeys(),rootEntity);
                Class fieldJavaClass = fieldPath.getJavaType();
                String fieldName = this.getFieldNameByKeys(criteria.getKeys());

                if(fieldJavaClass == Date.class){
                    try {
                        fieldValue = format.parse(fieldValue.toString());
                    } catch (ParseException e) {
                        fieldValue = new Date();
                    }
                }
                else if(fieldJavaClass == String.class){
                    fieldValue = new StringBuilder("%").append(fieldValue).append("%").toString();
                    restrictions.add(Restrictions.like(fieldName,fieldValue));
                    return;
                }
                else if(fieldJavaClass.getSuperclass() == Enum.class){
                    fieldValue = Enum.valueOf(fieldJavaClass,fieldValue.toString());
                }

                String operationOnField = criteria.getOperation();

                if (operationOnField.equalsIgnoreCase(">"))
                    restrictions.add(Restrictions.ge(fieldName,fieldValue));
                else if (operationOnField.equalsIgnoreCase("<"))
                    restrictions.add(Restrictions.le(fieldName,fieldValue));
                else
                    restrictions.add(Restrictions.eq(fieldName,fieldValue));
            });
    }

    @Override
    public Predicate toPredicate(Root<V> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates=new ArrayList<>();
        criteria.forEach((criteria) -> {
                    Object fieldValue = criteria.getValue();
                    Path fieldPath = this.getFieldPathByKeys(criteria.getKeys(),root);
                    Class fieldJavaClass = fieldPath.getJavaType();
                    if(fieldJavaClass == Date.class)
                        try {
                            fieldValue = format.parse(criteria.getValue().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    else if(fieldJavaClass == String.class) {
                        fieldValue = new StringBuilder("%").append(fieldValue).append("%").toString();
                        predicates.add(builder.like(fieldPath, fieldValue.toString()));
                        return;
                    }

                    String operationOnField = criteria.getOperation();

            if (operationOnField.equalsIgnoreCase(">"))
                predicates.add(builder.greaterThanOrEqualTo(fieldPath, fieldValue.toString()));
            else if(operationOnField.equalsIgnoreCase("<"))
                predicates.add(builder.greaterThanOrEqualTo(fieldPath, fieldValue.toString()));
            else
                predicates.add(builder.equal(fieldPath, criteria.getValue()));
        }
        );
        Predicate[] predicatesTable = new Predicate[predicates.size()];
        return builder.and(predicates.toArray(predicatesTable));
    }

    private <Y, X> Path<Y> getFieldPathByKeys(List<String> keys, Root<X> root)
    {
        Path<Y> path=root.get(keys.get(0));
        for(int i=1;i<keys.size();i++)
            path=path.get(keys.get(i));
        return path;
    }

    private String getFieldNameByKeys(List<String> keys)
    {
        if(keys.size()>=2)
            return new StringBuilder(keys.get(keys.size()-2)).append(".").append(keys.get(keys.size()-1)).toString();
        else
            return keys.get(keys.size()-1);
    }
}
