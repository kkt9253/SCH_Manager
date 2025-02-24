package sch_helper.sch_manager.domain.menu.dto.converter;

import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuConverter {

    public static List<MealResponseDTO> toMealResponseDTOs(List<Menu> menus) {

        return menus.stream()
                .map(MealResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public static List<DailyMealResponseDTO> getDailyMealResponseDTOsByMenus(List<Menu> menus) {

        List<DailyMealResponseDTO> responseDTOs = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<MealResponseDTO> mealResponseDTOs = new ArrayList<>();
            for (Menu menu : menus) {
                if (menu.getDayOfWeek() == dayOfWeek) {
                    mealResponseDTOs.add(MealResponseDTO.fromEntity(menu));
                }
            }
            responseDTOs.add(new DailyMealResponseDTO(dayOfWeek.name(), mealResponseDTOs));
        }
        return responseDTOs;
    }
}
