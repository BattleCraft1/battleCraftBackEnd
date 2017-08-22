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
import java.util.stream.Collectors;

public class SearchSpecification<V> implements Specification<V>{

    public SearchSpecification() {
    }

    public SearchSpecification(List<SearchCriteria> criteries) {
        this.criteries = criteries;
    }

    private List<SearchCriteria> criteries;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public List<SearchCriteria> getCriteries() {
        return criteries;
    }

    public void setCriteries(List<SearchCriteria> criteries) {
        this.criteries = criteries;
    }

    public void toRestrictions(Criteria query, Root<V> root){
        criteries.forEach((criteria) -> {
            String field=criteria.getKeys().stream().map(Object::toString).collect(Collectors.joining("."));
            if (criteria.getOperation().equalsIgnoreCase(">")) {
                if (this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == Date.class)
                    try {
                        query.add(Restrictions.ge(getFieldByKeys(criteria.getKeys()),format.parse(criteria.getValue().toString())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                else
                    query.add(Restrictions.ge(getFieldByKeys(criteria.getKeys()),criteria.getValue()));
            }
            else if (criteria.getOperation().equalsIgnoreCase("<")) {
                if (this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == Date.class)
                    try {
                        query.add(Restrictions.le(getFieldByKeys(criteria.getKeys()),format.parse(criteria.getValue().toString())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                else
                    query.add(Restrictions.le(getFieldByKeys(criteria.getKeys()),criteria.getValue()));
            }
            else if (criteria.getOperation().equalsIgnoreCase(":")) {
                if(this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == String.class ||
                        this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == Enum.class)
                    query.add(Restrictions.like(getFieldByKeys(criteria.getKeys()),new StringBuilder("%").append(criteria.getValue()).append("%").toString()));
                else
                    query.add(Restrictions.eq(getFieldByKeys(criteria.getKeys()),criteria.getValue()));
            }
        });
    }

    @Override
    public Predicate toPredicate(Root<V> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates=new ArrayList<>();
        criteries.forEach((criteria) -> {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if(this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == Date.class)
                try {
                    predicates.add(builder.greaterThanOrEqualTo(this.getFieldByKeys(criteria.getKeys(), root), format.parse(criteria.getValue().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            else
                predicates.add(builder.greaterThanOrEqualTo(
                        this.getFieldByKeys(criteria.getKeys(), root), criteria.getValue().toString()));

        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            if(this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == Date.class)
                try {
                    predicates.add(builder.lessThanOrEqualTo(this.getFieldByKeys(criteria.getKeys(),root), format.parse(criteria.getValue().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            else
                predicates.add(builder.lessThanOrEqualTo(
                        this.getFieldByKeys(criteria.getKeys(),root), criteria.getValue().toString()));
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (this.getFieldByKeys(criteria.getKeys(),root).getJavaType() == String.class) {
                predicates.add(builder.like(
                        this.getFieldByKeys(criteria.getKeys(),root), new StringBuilder("%").append(criteria.getValue()).append("%").toString()));
            } else {
                predicates.add(builder.equal(this.getFieldByKeys(criteria.getKeys(),root), criteria.getValue()));
            }
        }
        }
        );

        Predicate[] predicatesTable = new Predicate[predicates.size()];
        return builder.and(predicates.toArray(predicatesTable));
    }

    private <Y, X> Path<Y> getFieldByKeys(List<String> keys, Root<X> root)
    {
        Path<Y> path=root.get(keys.get(0));
        for(int i=1;i<keys.size();i++)
            path=path.get(keys.get(i));
        return path;
    }

    private String getFieldByKeys(List<String> keys)
    {
        if(keys.size()>=2)
            return new StringBuilder(keys.get(keys.size()-2)).append(".").append(keys.get(keys.size()-1)).toString();
        else
            return keys.get(keys.size()-1);
    }
}
