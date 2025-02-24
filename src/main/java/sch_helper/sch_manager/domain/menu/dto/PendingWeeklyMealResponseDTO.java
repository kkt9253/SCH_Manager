package sch_helper.sch_manager.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PendingWeeklyMealResponseDTO {

    private String weekMealImg;

    private List<DailyMealResponseDTO> dailyMeals;
}
