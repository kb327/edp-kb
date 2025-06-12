package edp.wat.edu.pl.projectkb.service;

import edp.wat.edu.pl.projectkb.model.FavoriteLocation;
import edp.wat.edu.pl.projectkb.model.User;
import edp.wat.edu.pl.projectkb.persictence.DbPersistenceUnit;
import edp.wat.edu.pl.projectkb.session.SessionContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FavoriteLocationService {

    public boolean addFavoriteLocation(String name, String state, String country, double lat, double lon) {
        EntityManager em = DbPersistenceUnit.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Long userId = SessionContext.getLoggedInUserId();
            User user = em.find(User.class, userId);

            // Sprawdzenie, czy już istnieje
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(f) FROM FavoriteLocation f WHERE f.name = :name AND f.state = :state AND f.country = :country AND f.lat = :lat AND f.lon = :lon AND f.user.userId = :userId",
                    Long.class
            );
            query.setParameter("name", name);
            query.setParameter("state", state);
            query.setParameter("country", country);
            query.setParameter("lat", lat);
            query.setParameter("lon", lon);
            query.setParameter("userId", userId);

            Long count = query.getSingleResult();

            if (count > 0) {
                tx.rollback();
                return false; // już istnieje
            }

            FavoriteLocation location = FavoriteLocation.builder()
                    .name(name)
                    .state(state)
                    .country(country)
                    .lat(lat)
                    .lon(lon)
                    .user(user)
                    .build();

            em.persist(location);
            tx.commit();
            return true; // dodano pomyślnie

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<FavoriteLocation> getUserFavorites() {
        EntityManager em = DbPersistenceUnit.getEntityManager();
        try {
            return em.createQuery("SELECT f FROM FavoriteLocation f WHERE f.user.userId = :uid", FavoriteLocation.class)
                    .setParameter("uid", SessionContext.getLoggedInUserId())
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public void deleteFavorite(Long locationId) {
        EntityManager em = DbPersistenceUnit.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            FavoriteLocation loc = em.find(FavoriteLocation.class, locationId);
            if (loc != null && loc.getUser().getUserId().equals(SessionContext.getLoggedInUserId())) {
                em.remove(loc);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}