package edp.wat.edu.pl.projectkb.service;

import edp.wat.edu.pl.projectkb.model.WeatherHistory;
import edp.wat.edu.pl.projectkb.persictence.DbPersistenceUnit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

public class WeatherHistoryService {

    public boolean save(WeatherHistory history) {
        EntityManager em = DbPersistenceUnit.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            boolean exists = existsForUserAndDateAndLocation(
                    history.getUser().getUserId(),
                    history.getDate(),
                    history.getLat(),
                    history.getLon(),
                    history.getLocationName(),
                    history.getState(),
                    history.getCountry(),
                    em
            );

            if (exists) {
                return false; // Rekord już istnieje
            }

            transaction.begin();
            em.persist(history);
            transaction.commit();
            return true; // Rekord został zapisany

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    private boolean existsForUserAndDateAndLocation(Long userId, LocalDate date,
                                                    double lat, double lon,
                                                    String name, String state, String country,
                                                    EntityManager em) {
        Long count = em.createQuery("""
                SELECT COUNT(w) FROM WeatherHistory w 
                WHERE w.user.userId = :userId 
                  AND w.date = :date
                  AND w.lat = :lat 
                  AND w.lon = :lon
                  AND w.locationName = :name
                  AND w.state = :state
                  AND w.country = :country
            """, Long.class)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .setParameter("name", name)
                .setParameter("state", state)
                .setParameter("country", country)
                .getSingleResult();

        return count > 0;
    }
}
