package pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.implementations;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.page.implementations.PaginatorImpl;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.page.interfaces.Paginator;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.repositoryPageAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.AnyEntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GetPageAssistantImpl implements GetPageAssistant {
    @PersistenceContext
    private EntityManager entityManager;

    private Session hibernateSession;
    private Root root;
    private Paginator paginator;

    private Field[] projectionFields;
    private ProjectionList projectionList = Projections.projectionList();
    private List<Join> joins = new ArrayList<>();
    private Criteria criteria;
    private List<Criterion> whereConditions = new ArrayList<>();

    private String transactionId;
    private Class entityClass;

    @Autowired
    public GetPageAssistantImpl(){

    }

    @Override
    public GetPageAssistant select(Field... fields){
        this.projectionFields = fields;
        return this;
    }

    @Override
    public GetPageAssistant join(Join... aliases){
        this.joins.addAll(Arrays.asList(aliases));
        return this;
    }

    @Override
    public GetPageAssistant from(Class entityClass){
        this.hibernateSession = (Session) entityManager.getDelegate();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        this.paginator = new PaginatorImpl(entityClass);
        this.transactionId = entityClass.getSimpleName().toLowerCase();
        this.entityClass = entityClass;
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
        this.criteria = this.hibernateSession.createCriteria(entityClass, this.transactionId);
        Arrays.stream(projectionFields).forEach(
                field -> projectionList.add(field.operation.apply(getFieldFullName(field.name)),field.value)
        );
        return this;
    }

    @Override
    public GetPageAssistant where(List<SearchCriteria> searchCriteria){
        joins.forEach(join -> criteria.createAlias(getFieldFullName(join.name),join.value));
        searchCriteria.forEach((condition) -> {
            String fieldName = condition.getName();
            List<Object> fieldValue = condition.getValue(root);
            String operationOnField = condition.getOperation();
            if(fieldValue.get(0) instanceof String)
                whereConditions.add(Restrictions.like(fieldName, fieldValue.get(0)));
            else if(fieldValue.get(0) instanceof Enum){
                SimpleExpression[] orCriteries = fieldValue.stream().map(value -> Restrictions.eq(fieldName, value))
                        .toArray(SimpleExpression[]::new);
                whereConditions.add(Restrictions.or(orCriteries));
            }
            else if (operationOnField.equalsIgnoreCase(">"))
                whereConditions.add(Restrictions.ge(fieldName, fieldValue.get(0)));
            else if (operationOnField.equalsIgnoreCase("<"))
                whereConditions.add(Restrictions.le(fieldName, fieldValue.get(0)));
            else
                whereConditions.add(Restrictions.eq(fieldName, fieldValue.get(0)));
        });

        return this;
    }

    @Override
    public GetPageAssistant groupBy(String... groupByFields){
        Arrays.stream(groupByFields).forEach(
                field -> projectionList.add(Projections.groupProperty(getFieldFullName(field)))
        );
        this.criteria.setProjection(projectionList).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return this;
    }

    @Override
    public Page execute(String countProperty,Pageable requestedPage){
        whereConditions.forEach(
                whereCondition -> criteria.add(whereCondition)
        );
        Long count = this.count(countProperty);
        return this.paginator.createPage(count,criteria,requestedPage);
    }

    private Long count(String countProperty){
        this.transactionId = new StringBuilder(transactionId).append("Count").toString();
        Criteria criteriaCount = hibernateSession.createCriteria(entityClass,transactionId);
        criteriaCount.setProjection(Projections.countDistinct(getFieldFullName(countProperty)));
        joins.forEach(join -> criteriaCount.createAlias(getFieldFullName(join.name),join.value));
        whereConditions.forEach(
                criteriaCount::add
        );
        Long count = (Long) criteriaCount.uniqueResult();
        if(count==0)
            throw new AnyEntityNotFoundException();
        return count;
    }

    private String getFieldFullName(String fieldName){
        if(fieldName.contains("."))
            return fieldName;
        else
            return new StringBuilder(transactionId).append(".").append(fieldName).toString();
    }
}
