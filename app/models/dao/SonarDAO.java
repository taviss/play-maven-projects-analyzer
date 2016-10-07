package models.dao;

import models.Sonar;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by octavian.salcianu on 8/25/2016.
 */
public class SonarDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public SonarDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public Sonar get(Long id) {
        return this.em.find(Sonar.class, id);
    }

    public void delete(Sonar sonar) {
        em.remove(sonar);
    }

    public List<Sonar> getAll() {
        CriteriaQuery<Sonar> criteriaQuery = this.criteriaBuilder.createQuery(Sonar.class);
        Root<Sonar> root = criteriaQuery.from(Sonar.class);
        criteriaQuery.select(root);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<Sonar> resultList = (List<Sonar>) query.getResultList();
        return resultList;
    }

    public Sonar create(Sonar sonar) {
        sonar.setId(null);
        em.persist(sonar);
        return sonar;
    }

    public Sonar update(Sonar sonar) {
        return em.merge(sonar);
    }
}
