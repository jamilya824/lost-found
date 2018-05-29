package com.example.try4.dao;

import com.example.try4.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;
@Repository
@Transactional
public class ApplicationDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ApplicationDAO(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private EntityManager entityManager;

    public Application addApp(Application app) {
        app.setDate(Calendar.getInstance().getTime());
        this.entityManager.persist(app);
        this.entityManager.flush();
        return app;
    }
    public List<Application> getApp() {
        try {
            String sql = "Select e from " + Application.class.getName() + " e ";
            Query query = entityManager.createQuery(sql, Application.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public Application findAppId(long id) {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + " Where e.id = :id ";
            Query query = entityManager.createQuery(sql, Application.class);
            query.setParameter("id", id);
            Application application=(Application) query.getSingleResult();
            return  application;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Application> findFoundLost(String status) {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + " Where e.status= :status ";

            Query query = entityManager.createQuery(sql, Application.class);
            query.setParameter("status", status);

            return  query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public List<Application> findByCategory(String status) {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + " Where e.category= :category ";

            Query query = entityManager.createQuery(sql, Application.class);
            query.setParameter("category", status);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public List<Application> findPopular() {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + "order by e.view desc ";
            Query query = entityManager.createQuery(sql, Application.class);
            return (List<Application>) query.setFirstResult(0).setMaxResults(3).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public List<Application> search(String word) {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + "where concat(e.title,e.category,e.status) like '%"+word+"%'";
            Query query = entityManager.createQuery(sql, Application.class);
            return (List<Application>) query.setFirstResult(0).setMaxResults(3).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public Application updateCommentNum(long id,int n){
        Application application=findAppId(id);
        application.setView(application.getView()-1);
        System.out.println(application.getComNumber()+" "+n);
        application.setComNumber(application.getComNumber()+n);
        return addApp(application);
    }


    public List<Application> getUsersApplication(String username) {
        try {
            String sql = "Select e from " + Application.class.getName() + " e " //
                    + "where e.usarname= :username";
            Query query = entityManager.createQuery(sql, Application.class);
            query.setParameter("username",username);
            return  query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public void deleteApp(long id){
        this.entityManager.remove(findAppId(id));
        this.entityManager.flush();
        this.entityManager.clear();
    }


}
