package edp.wat.edu.pl.projectkb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "weather_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String locationName;

    private String state;

    private String country;

    private double lat;

    private double lon;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String weatherData;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return locationName + ", " + state + ", " + country + " - " + date;
    }
}

