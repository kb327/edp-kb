package edp.wat.edu.pl.projectkb.model.weatherApiModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecast {
    private LocalDate date;
    private String dayName;
    private double avgDayTemp;
    private double avgNightTemp;
    private String icon;
}
