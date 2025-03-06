package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.Builder;
import lombok.Data;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;


@Data
@Builder
public class MealResponse {

    private MealType mealType;

    private MenuStatus menuStatus;

    private String mainMenu;

    private String subMenu;

    private String operatingStartTime;

    private String operatingEndTime;

}
