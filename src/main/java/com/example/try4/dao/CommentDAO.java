package com.example.try4.dao;


import com.example.try4.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;

@Repository
@Transactional
public class CommentDAO {

    @Autowired
    private EntityManager entityManager;

    public Comment addComment(Comment comment) {
        comment.setDateCom(Calendar.getInstance().getTime());
        this.entityManager.persist(comment);
        this.entityManager.flush();
        return comment;
    }
    public List<Comment> findComment(long id) {
        try {
            String sql = "Select e from " + Comment.class.getName() + " e " //
                    + " Where e.id_app = :id_app ";

            Query query = entityManager.createQuery(sql, Comment.class);
            query.setParameter("id_app", id);

            return  query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public Comment findCommentId(long id) {
        try {
            String sql = "Select e from " + Comment.class.getName() + " e " //
                    + " Where e.id = :id ";

            Query query = entityManager.createQuery(sql, Comment.class);
            query.setParameter("id", id);
            return (Comment) query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
    public void deleteComment(long id){
        this.entityManager.remove(findCommentId(id));
        this.entityManager.flush();
        this.entityManager.clear();
    }

}
