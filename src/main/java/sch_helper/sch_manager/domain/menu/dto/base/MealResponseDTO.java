package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.enums.MealType;

@Getter
@Setter
@AllArgsConstructor
public class MealResponseDTO {

    private MealType mealType;
    private String operatingStartTime;
    private String operatingEndTime;
    private String mainMenu;
    private String subMenu;

    public static MealResponseDTO fromEntity(Menu menu) {

        return new MealResponseDTO(
                menu.getMealType(),
                menu.getOperatingStartTime(),
                menu.getOperatingEndTime(),
                menu.getMainMenu(),
                menu.getSubMenu()
        );
    }
}
