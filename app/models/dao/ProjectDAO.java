package models.dao;

import models.Project;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import play.Configuration;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by octavian.salcianu on 8/17/2016.
 */
public class ProjectDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public ProjectDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public Project get(Long id) {
        return this.em.find(Project.class, id);
    }

    public void delete(Project project) {
        em.remove(project);
    }

    public Project create(Project project) {
        project.setId(null);
        em.persist(project);
        return project;
    }

    public Project update(Project project) {
        return em.merge(project);
    }

    public List<Project> getAll(int page) {
        CriteriaQuery<Project> criteriaQuery = this.criteriaBuilder.createQuery(Project.class);
        Root<Project> root = criteriaQuery.from(Project.class);
        criteriaQuery.select(root);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("inputDate")));
        Query query = this.em.createQuery(criteriaQuery).setFirstResult(page*10).setMaxResults(10);
        @SuppressWarnings("unchecked")
        List<Project> resultList = (List<Project>) query.getResultList();
        return resultList;
    }

    public Page findByName(int page, Set<Map.Entry<String, String[]>> queryString) {
        int pageSize = Configuration.root().getInt("maxPageSize");
        if(page < 1) page = 1;
        CriteriaQuery<Project> criteriaQuery = this.criteriaBuilder.createQuery(Project.class);
        Root<Project> root = criteriaQuery.from(Project.class);

        String name = "";
        String author = "";
        for (Map.Entry<String,String[]> entry : queryString) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            switch (key) {
                case "name": {
                    name = value[0].toLowerCase();
                    break;
                }

                case "author": {
                    author = value[0].toLowerCase();
                    break;
                }

                default:
                    break;
            }
        }
        Query query;
        Long total = 0L;
        if(!name.equals("") || !author.equals("")) {
            try {
                FullTextEntityManager fullTextEntityManager =
                        org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
                QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                        .buildQueryBuilder().forEntity(Project.class).get();

                org.apache.lucene.search.Query luceneQuery;
                if (!name.equals("")) {
                    luceneQuery = qb
                            .keyword()
                            .onFields("projectName")
                            .matching(name)
                            .createQuery();
                } else {
                    luceneQuery = qb
                            .keyword()
                            .onFields("name")
                            .matching(author)
                            .createQuery();
                }

                query =
                        fullTextEntityManager.createFullTextQuery(luceneQuery, Project.class);

                total = (long) query.getResultList().size();

                query =
                        fullTextEntityManager.createFullTextQuery(luceneQuery, Project.class).setFirstResult((page - 1) * pageSize).setMaxResults(pageSize);

            //In case the queries contain only stop words, fetch all the results, like a normal listing
            } catch (EmptyQueryException e) {
                Query totalQuery = this.em.createQuery("SELECT COUNT(p.id) FROM Project p");
                total = (Long) totalQuery.getSingleResult();
                criteriaQuery.select(root);
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("inputDate")));
                query = this.em.createQuery(criteriaQuery).setFirstResult((page-1)*pageSize).setMaxResults(pageSize);
            }
        } else {
            Query totalQuery = this.em.createQuery("SELECT COUNT(p.id) FROM Project p");
            total = (Long) totalQuery.getSingleResult();
            criteriaQuery.select(root);
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("inputDate")));
            query = this.em.createQuery(criteriaQuery).setFirstResult((page-1)*pageSize).setMaxResults(pageSize);
        }

        @SuppressWarnings("unchecked")
        List<Project> foundProjects = (List<Project>) query.getResultList();
        return new Page(foundProjects, total, page, pageSize);
    }

    public Page getPage(int page) {
        int pageSize = Configuration.root().getInt("maxPageSize");
        if(page < 1) page = 1;
        CriteriaQuery<Project> criteriaQuery = this.criteriaBuilder.createQuery(Project.class);
        Root<Project> root = criteriaQuery.from(Project.class);

        Query totalQuery = this.em.createQuery("SELECT COUNT(p.id) FROM Project p");
        Long total = (Long) totalQuery.getSingleResult();

        criteriaQuery.select(root);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("inputDate")));
        Query query = this.em.createQuery(criteriaQuery).setFirstResult((page-1)*pageSize).setMaxResults(pageSize);
        @SuppressWarnings("unchecked")
        List<Project> resultList = (List<Project>) query.getResultList();

        return new Page(resultList, total, page, pageSize);
    }

    private class Page {

        private final int pageSize;
        private final Long totalRowCount;
        private final int pageIndex;
        private final List<Project> list;

        public Page(List<Project> data, Long total, int page, int pageSize) {
            this.list = data;
            this.totalRowCount = total;
            this.pageIndex = page;
            this.pageSize = pageSize;
        }

        public Long getTotalRowCount() {
            return totalRowCount;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public List<Project> getList() {
            return list;
        }

        public boolean getHasPrev() {
            return pageIndex > 1;
        }

        public boolean getHasNext() {
            return ((float)totalRowCount/pageSize) > pageIndex;
        }

        public String getDisplayXtoYofZ() {
            int start = ((pageIndex - 1) * pageSize + 1);
            int end = start + Math.min(pageSize, list.size()) - 1;
            return start + " to " + end + " of " + totalRowCount;
        }

    }

}
