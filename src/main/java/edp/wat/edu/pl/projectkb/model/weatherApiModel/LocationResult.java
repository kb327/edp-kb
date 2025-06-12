package edp.wat.edu.pl.projectkb.model.weatherApiModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResult {
    private String name;
    private String country;
    private String state;
    private double lat;
    private double lon;
}
