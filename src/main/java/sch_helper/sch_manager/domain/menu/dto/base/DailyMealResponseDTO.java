package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DailyMealResponseDTO {

    private String dayOfWeek;

    private List<MealResponseDTO> meals;
}
