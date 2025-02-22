package sch_helper.sch_manager.domain.menu.dto.converter;

import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;

import java.util.List;
import java.util.stream.Collectors;

public class MenuConverter {

    public static List<MealResponseDTO> toMealResponseDTOs(List<Menu> menus) {

        return menus.stream()
                .map(MealResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }


}
