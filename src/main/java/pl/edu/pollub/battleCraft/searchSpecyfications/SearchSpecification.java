package pl.edu.pollub.battleCraft.searchSpecyfications;


import org.springframework.data.jpa.domain.Specification;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @Override
    public Predicate toPredicate
            (Root<V> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates=new ArrayList<>();
        criteries.forEach((criteria) -> {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if(criteria.getType().equals("Date"))
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
            if(criteria.getType().equals("Date"))
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
                        this.getFieldByKeys(criteria.getKeys(),root), "%" + criteria.getValue() + "%"));
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
}
