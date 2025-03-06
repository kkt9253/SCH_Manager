package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.Builder;
import lombok.Data;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.List;

@Data
@Builder
public class DailyMenuResponse {

    private DayOfWeek dayOfWeek;

    private byte[] dailyMenuImg;

    private List<MealResponse> mealResponseDTOList;

}
