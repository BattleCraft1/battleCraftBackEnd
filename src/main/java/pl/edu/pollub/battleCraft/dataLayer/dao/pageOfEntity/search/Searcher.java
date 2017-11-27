package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.page.Pager;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyEntityNotFoundException;

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
public class Searcher{
    @PersistenceContext
    private EntityManager entityManager;

    private Session hibernateSession;
    private Root root;
    private Pager paginator;

    private Field[] projectionFields;
    private ProjectionList projectionList = Projections.projectionList();
    private List<Join> joins = new ArrayList<>();
    private Criteria criteria;
    private List<Criterion> whereConditions = new ArrayList<>();

    private String transactionId;
    private Class entityClass;

    public Searcher select(Field... fields){
        this.projectionFields = fields;
        return this;
    }

    public Searcher join(Join... aliases){
        this.joins.addAll(Arrays.asList(aliases));
        return this;
    }

    @SuppressWarnings("unchecked")
    public Searcher from(Class entityClass){
        this.hibernateSession = (Session) entityManager.getDelegate();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        this.paginator = new Pager(entityClass);
        this.transactionId = entityClass.getSimpleName().toLowerCase();
        this.entityClass = entityClass;
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        this.root = criteriaQuery.from(entityClass);
        this.criteria = this.hibernateSession.createCriteria(entityClass, this.transactionId);
        Arrays.stream(projectionFields).forEach(
                field -> projectionList.add(field.getOperation().apply(getFieldFullName(field.getName())),field.getValue())
        );
        return this;
    }

    public Searcher where(List<SearchCriteria> searchCriteria){
        joins.forEach(join -> criteria.createAlias(getFieldFullName(join.getName()),join.getValue()));
        searchCriteria.forEach((condition) -> {
            String fieldName = condition.getName();
            List<Object> fieldValue = condition.getValue(root);
            String operationOnField = condition.getOperation();
            if(fieldValue.get(0) instanceof String && operationOnField.equalsIgnoreCase(":"))
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
            else if (operationOnField.equalsIgnoreCase("not in"))
                whereConditions.add(Restrictions.not(Restrictions.in(fieldName, fieldValue)));
            else if (operationOnField.equalsIgnoreCase("participated by"))
                whereConditions.add(Subqueries.in("name", this.tournamentsNamesParticipatedByUser(fieldValue.get(0))));
            else if (operationOnField.equalsIgnoreCase("organized by"))
                whereConditions.add(Subqueries.in("name", this.tournamentsNamesOrganizedByUser(fieldValue.get(0))));
            else if (operationOnField.equalsIgnoreCase("not participate"))
                whereConditions.add(Subqueries.propertyNotIn("name", this.userNamesWhoNotParticipateToThisTournamentSubQuery(fieldValue.get(0))));
            else
                whereConditions.add(Restrictions.eq(fieldName, fieldValue.get(0)));
        });

        return this;
    }

    public Searcher groupBy(String... groupByFields){
        Arrays.stream(groupByFields).forEach(
                field -> projectionList.add(Projections.groupProperty(getFieldFullName(field)))
        );
        this.criteria.setProjection(projectionList);
        this.criteria.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return this;
    }

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
        joins.forEach(join -> criteriaCount.createAlias(getFieldFullName(join.getName()),join.getValue()));
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

    private DetachedCriteria tournamentsNamesOrganizedByUser(Object fieldValue){
        return DetachedCriteria.forClass(Organizer.class)
                .createAlias("organizations","organizations")
                .createAlias("organizations.organizedTournament","organizedTournament")
                .add(Restrictions.like("name", fieldValue))
                .setProjection(Projections.property("organizedTournament.name"))
                .setProjection(Projections.groupProperty("organizedTournament.name"));
    }

    private DetachedCriteria tournamentsNamesParticipatedByUser(Object fieldValue){
        return DetachedCriteria.forClass(Player.class)
                .createAlias("participation","participation")
                .createAlias("participation.participatedTournament","participatedTournament")
                .add(Restrictions.like("name", fieldValue))
                .setProjection(Projections.property("participatedTournament.name"))
                .setProjection(Projections.groupProperty("participatedTournament.name"));
    }

    private DetachedCriteria userNamesWhoNotParticipateToThisTournamentSubQuery(Object fieldValue){
        return DetachedCriteria.forClass(Tournament.class)
                .createAlias("participation","participation")
                .createAlias("participation.player","player")
                .add(Restrictions.like("name", fieldValue))
                .setProjection(Projections.property("player.name"))
                .setProjection(Projections.groupProperty("player.name"));
    }
}
