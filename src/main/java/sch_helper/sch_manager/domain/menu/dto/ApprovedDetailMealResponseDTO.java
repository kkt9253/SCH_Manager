package sch_helper.sch_manager.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApprovedDetailMealResponseDTO {

    private String restaurantOperatingStartTime;

    private String restaurantOperatingEndTime;

    private boolean isActive;

    List<DailyMealResponseDTO> dailyMeals;
}
