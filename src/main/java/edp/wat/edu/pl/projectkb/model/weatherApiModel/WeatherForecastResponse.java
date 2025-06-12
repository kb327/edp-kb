package edp.wat.edu.pl.projectkb.model.weatherApiModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastResponse {
    @JsonProperty("dt")
    private Long dateTime;

    @JsonProperty("main")
    private MainModel main;

    @JsonProperty("weather")
    private List<WeatherObject> list;

    @JsonProperty("dt_txt")
    private String dateText;

    @JsonProperty("wind")
    private WindModel wind;

    @JsonProperty("sys")
    private SysModel sys;

    @JsonProperty("rain")
    private RainModel rain;
}
