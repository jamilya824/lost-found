package com.example.try4.dao;

import com.example.try4.entity.Comment;
import com.example.try4.entity.Post;
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
public class PostDAO {

    @Autowired
    private EntityManager entityManager;

    public Post addPost(Post post) {
        post.setDatePost(Calendar.getInstance().getTime());
        this.entityManager.persist(post);
        this.entityManager.flush();
        return post;
    }
    public List<Comment> findComment(String user) {
        try {
            String sql = "Select e from " + Post.class.getName() + " e " //
                    + " Where e.user = :user ";

            Query query = entityManager.createQuery(sql, Post.class);
            query.setParameter("user", user);

            return  query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    public void deletePost(long id){
        this.entityManager.remove(findPostId(id));
        this.entityManager.flush();
        this.entityManager.clear();
    }

    public Post findPostId(long id) {
        try {
            String sql = "Select e from " + Post.class.getName() + " e " //
                    + " Where e.id = :id ";

            Query query = entityManager.createQuery(sql, Post.class);
            query.setParameter("id", id);
            return (Post) query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }
}
