package sch_helper.sch_manager.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingDailyMealResponseDTO {

    private String dayMealImg;
    private String weekMealImg;

    private DailyMealResponseDTO dailyMeal;
}
