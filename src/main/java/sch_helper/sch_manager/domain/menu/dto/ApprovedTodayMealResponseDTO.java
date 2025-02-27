package sch_helper.sch_manager.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;

@Getter
@AllArgsConstructor
public class ApprovedTodayMealResponseDTO {

    private String restaurantName;

    private boolean isActive;

    private DailyMealResponseDTO dailyMeals;
}

