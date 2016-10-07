package models.dao;

import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by octavian.salcianu on 8/24/2016.
 */
public class TestDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public TestDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }
}
