package sch_helper.sch_manager.domain.menu.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.List;

@Getter
public class DailyMealDTO {

    private DayOfWeek dayOfWeek;

    @JsonProperty("meals")
    private List<MealDTO> meals;
}
