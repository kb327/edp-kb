package edp.wat.edu.pl.projectkb.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;     // np. "Warszawa"
    private String state;    // np. "Mazowieckie"
    private String country;  // np. "PL"
    private double lat;
    private double lon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
