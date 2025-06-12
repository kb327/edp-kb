package edp.wat.edu.pl.projectkb.model.weatherApiModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainModel {
    @JsonProperty("temp")
    private Double temperature;
    @JsonProperty("feels_like")
    private Double feelsLike;
    @JsonProperty("humidity")
    private Integer humidity;
}
