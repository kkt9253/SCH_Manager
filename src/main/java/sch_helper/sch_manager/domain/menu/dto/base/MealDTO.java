package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.Getter;
import sch_helper.sch_manager.domain.menu.enums.MealType;

@Getter
public class MealDTO {

    private MealType mealType;
    private String operatingStartTime;
    private String operatingEndTime;
    private String mainMenu;
    private String subMenu;
}
