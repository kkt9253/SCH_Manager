package sch_helper.sch_manager.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuRepository;

@Component
@RequiredArgsConstructor
public class MenuUtil {

    private final MenuRepository menuRepository;



    public void saveDailyMeal(Restaurant restaurant, DailyMealDTO dailyMealDTO, MenuStatus menuStatus) {

        DayOfWeek dayOfWeek = dailyMealDTO.getDayOfWeek();

        for (MealDTO mealDTO : dailyMealDTO.getMeals()) {

            String uniqueName = restaurant.getId() + "_" + dayOfWeek + "_" + mealDTO.getMealType();
            if (menuRepository.existsByUnique(uniqueName)) {
                menuRepository.deleteByUnique(uniqueName);
            }

            Menu menu = new Menu();
            menu.setRestaurant(restaurant);
            menu.setDayOfWeek(dayOfWeek);
            menu.setMealType(mealDTO.getMealType());
            menu.setOperatingStartTime(mealDTO.getOperatingStartTime());
            menu.setOperatingEndTime(mealDTO.getOperatingEndTime());
            menu.setMainMenu(mealDTO.getMainMenu());
            menu.setSubMenu(mealDTO.getSubMenu());
            menu.setUnique(uniqueName);
            menu.setMenuStatus(menuStatus);

            System.out.println("menu: " + menu);

            menuRepository.save(menu);
        }
    }
}
