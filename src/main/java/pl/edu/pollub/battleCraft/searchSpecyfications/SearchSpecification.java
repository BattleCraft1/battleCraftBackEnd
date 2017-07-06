package pl.edu.pollub.battleCraft.searchSpecyfications;


import org.springframework.data.jpa.domain.Specification;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecification<V> implements Specification<V>{

    public SearchSpecification() {
    }

    public SearchSpecification(List<SearchCriteria> criteries) {
        this.criteries = criteries;
    }

    private List<SearchCriteria> criteries;

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
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString()));
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString()));
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                predicates.add(builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
            } else {
                predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
            }
        }
        }
        );

        Predicate[] predicatesTable = new Predicate[predicates.size()];
        return builder.and(predicates.toArray(predicatesTable));
    }
}
