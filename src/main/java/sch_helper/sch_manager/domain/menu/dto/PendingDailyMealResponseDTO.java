package sch_helper.sch_manager.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;

@Getter
//@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingDailyMealResponseDTO {

    private String dayMealImg;
    private String weekMealImg;

    private DailyMealResponseDTO dailyMeals;
}
