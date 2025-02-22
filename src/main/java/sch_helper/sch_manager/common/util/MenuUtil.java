package sch_helper.sch_manager.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealRequestDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuUtil {

    private final MenuRepository menuRepository;


    public void saveDailyMeal(Restaurant restaurant, DailyMealRequestDTO dailyMealRequestDTO, MenuStatus menuStatus) {

        DayOfWeek dayOfWeek = dailyMealRequestDTO.getDayOfWeekEnum();

        for (MealRequestDTO mealRequestDTO : dailyMealRequestDTO.getMeals()) {

            String uniqueName = restaurant.getId() + "_" + dayOfWeek + "_" + mealRequestDTO.getMealType() + "_" + menuStatus;

            Optional<Menu> existMenu = menuRepository.findByUnique(uniqueName);

            Menu menu = existMenu.orElse(new Menu());
            menu.setRestaurant(restaurant);
            menu.setDayOfWeek(dayOfWeek);
            menu.setMealType(mealRequestDTO.getMealTypeEnum());
            menu.setOperatingStartTime(mealRequestDTO.getOperatingStartTime());
            menu.setOperatingEndTime(mealRequestDTO.getOperatingEndTime());
            menu.setMainMenu(mealRequestDTO.getMainMenu());
            menu.setSubMenu(mealRequestDTO.getSubMenu());
            menu.setUnique(uniqueName);
            menu.setMenuStatus(menuStatus);

            menuRepository.save(menu);
        }
    }
}
