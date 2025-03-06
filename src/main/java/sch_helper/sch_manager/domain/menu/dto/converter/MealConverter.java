package sch_helper.sch_manager.domain.menu.dto.converter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sch_helper.sch_manager.domain.menu.dto.base.MealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponse;
import sch_helper.sch_manager.domain.menu.entity.DailyMenu;
import sch_helper.sch_manager.domain.menu.entity.Meal;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class MealConverter {

    @Transactional(readOnly = true)
    public MealResponse toResponse(Meal meal) {
        return MealResponse.builder()
                .mealType(meal.getMealType())
                .menuStatus(meal.getMenuStatus())
                .mainMenu(meal.getMainMenu())
                .subMenu(meal.getSubMenu())
                .operatingStartTime(String.valueOf(meal.getOperatingStartTime()))
                .operatingEndTime(String.valueOf(meal.getOperatingEndTime()))
                .build();
    }

    public Meal toEntity(MealRequestDTO request, DailyMenu dailyMenu) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return Meal
                .builder()
                .mealType(request.getMealTypeEnum())
                .menuStatus(MenuStatus.PENDING)
                .mainMenu(request.getMainMenu())
                .subMenu(request.getSubMenu())
                .operatingStartTime(LocalTime.parse(request.getOperatingStartTime(), formatter))
                .operatingEndTime(LocalTime.parse(request.getOperatingEndTime(), formatter))
                .dailyMenu(dailyMenu)
                .build()
                ;
    }
}
