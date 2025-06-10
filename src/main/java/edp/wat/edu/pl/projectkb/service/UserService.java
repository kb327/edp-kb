package edp.wat.edu.pl.projectkb.service;

import edp.wat.edu.pl.projectkb.model.User;
import edp.wat.edu.pl.projectkb.persictence.DbPersistenceUnit;
import edp.wat.edu.pl.projectkb.session.SessionContext;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class UserService {
    public boolean register(String username, String password, String email, String name, String surname) {
        EntityManager em = DbPersistenceUnit.getEntityManager();

        User user = em.createQuery("SELECT u FROM User u WHERE u.username =: username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (user != null) {
            throw new IllegalArgumentException("Użytkownik już istnieje.");
        }

        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            User newUser = new User(username, hashedPassword, email, name, surname, LocalDateTime.now(), "USER");
            em.persist(newUser);

            tx.commit();
            SessionContext.setLoggedInUserId(newUser.getUserId());
            SessionContext.setLoggedInUserEmail(newUser.getEmail());
            return true;
        }catch(Exception e){
            if(tx.isActive()) tx.rollback();
        }finally{
            em.close();
        }
        return false;
    }
}
