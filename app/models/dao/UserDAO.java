package models.dao;

import models.User;
import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by octavian.salcianu on 8/29/2016.
 */
public class UserDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public UserDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    /**
     * Adds the newly created user to the database(luckily) and returns the new user with the new "id" from db
     * @param user : User
     * @return user : User
     */
    public User create(User user) {
        user.setId(null);
        em.persist(user);
        return user;
    }

    public User update(User user) {
        return em.merge(user);
    }

    public void delete(User user) {
        em.remove(user);
    }

    /**
     * Returns the user with "id"
     * @param id
     * @return User
     */
    public User get(Long id) {
        return this.em.find(User.class, id);
    }

    /**
     * Returns the user with specific username or null if it doesn't exist
     * @param userName
     * @return
     */
    public User getUserByName(String userName) {
        CriteriaQuery<User> criteriaQuery = this.criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root);
        Predicate userNameP = this.criteriaBuilder.equal(root.get("userName"), userName);

        criteriaQuery.where(userNameP);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<User> foundUsers = (List<User>) query.getResultList();

        if (foundUsers.isEmpty()) return null;
        else if (foundUsers.size() == 1) return foundUsers.get(0);
            //TBA: throw new exception
        else return null;

        //TBA
    }

    /**
     * Returns the user with specific email or null if it doesn't exist
     * @param userMail
     * @return
     */
    public User getUserByMail(String userMail) {
        CriteriaQuery<User> criteriaQuery = this.criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root);
        Predicate userMailP = this.criteriaBuilder.equal(root.get("userMail"), userMail);

        criteriaQuery.where(userMailP);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<User> foundUsers = (List<User>) query.getResultList();

        if (foundUsers.isEmpty()) return null;
        else if (foundUsers.size() == 1) return foundUsers.get(0);
            //TBA: throw new exception
        else return null;

        //TBA
    }

    /**
     * Returns the user with specific token or null if it doesn't exist
     * @param userToken
     * @return
     */
    public User getUserByToken(String userToken) {
        CriteriaQuery<User> criteriaQuery = this.criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root);
        Predicate userTokenP = this.criteriaBuilder.equal(root.get("userToken"), userToken);

        criteriaQuery.where(userTokenP);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<User> foundUsers = (List<User>) query.getResultList();
        if (foundUsers.isEmpty()) return null;
        else if (foundUsers.size() == 1) return foundUsers.get(0);
            //TBA: throw new exception
        else return null;
    }
}
