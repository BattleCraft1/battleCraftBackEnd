package pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.implementations;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.edu.pollub.battleCraft.data.repositories.helpers.page.implementations.PaginatorImpl;
import pl.edu.pollub.battleCraft.data.repositories.helpers.page.interfaces.Paginator;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.PageOfEntities.AnyEntityNotFoundException;

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
    private List<Join> aliases = new ArrayList<>();
    private Criteria criteria;
    private List<SimpleExpression> whereConditions = new ArrayList<>();

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
        this.aliases.addAll(Arrays.asList(aliases));
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
        searchCriteria.forEach((condition) -> {
            String fieldName = condition.getName();
            Object fieldValue = condition.getValue(root);
            String operationOnField = condition.getOperation();
            if(fieldValue instanceof String)
                whereConditions.add(Restrictions.like(fieldName, fieldValue));
            else if (operationOnField.equalsIgnoreCase(">"))
                whereConditions.add(Restrictions.ge(fieldName, fieldValue));
            else if (operationOnField.equalsIgnoreCase("<"))
                whereConditions.add(Restrictions.le(fieldName, fieldValue));
            else
                whereConditions.add(Restrictions.eq(fieldName, fieldValue));
        });

        return this;
    }

    @Override
    public GetPageAssistant groupBy(String... groupByFields){
        Arrays.stream(groupByFields).forEach(
                field -> projectionList.add(Projections.groupProperty(getFieldFullName(field)))
        );
        aliases.forEach(alias -> criteria.createAlias(getFieldFullName(alias.name),alias.value));
        this.criteria.setProjection(projectionList).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return this;
    }

    @Override
    public Page execute(Pageable requestedPage){
        whereConditions.forEach(
                whereCondition -> criteria.add(whereCondition)
        );
        Long count = this.count();
        return this.paginator.createPage(count,criteria,requestedPage);
    }

    private Long count(){
        this.transactionId = new StringBuilder(transactionId).append("Count").toString();
        Criteria criteriaCount = hibernateSession.createCriteria(entityClass,transactionId);
        criteriaCount.setProjection(Projections.countDistinct(getFieldFullName("id")));
        aliases.forEach(alias -> criteriaCount.createAlias(getFieldFullName(alias.name),alias.value));
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
