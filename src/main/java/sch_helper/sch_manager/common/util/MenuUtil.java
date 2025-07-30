package sch_helper.sch_manager.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealRequestDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.repository.MenuRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuUtil {

    private final MenuRepository menuRepository;

    @Transactional
    public void saveDailyMeal(Restaurant restaurant, String weekStartDate, DailyMealRequestDTO dailyMealRequestDTO, MenuStatus menuStatus) {

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek());

        for (MealRequestDTO mealRequestDTO : dailyMealRequestDTO.getMeals()) {

            Menu menu = menuRepository.findByRestaurantIdAndWeekStartDateAndDayOfWeekAndMealTypeAndMenuStatus(
                    restaurant.getId(),
                    LocalDate.parse(weekStartDate),
                    dayOfWeek,
                    MealType.valueOf(mealRequestDTO.getMealType()),
                    menuStatus
            ).map(existMenu -> {
                existMenu.setRestaurant(restaurant);
                existMenu.setDayOfWeek(dayOfWeek);
                existMenu.setMealType(mealRequestDTO.getMealTypeEnum());
                existMenu.setOperatingStartTime(mealRequestDTO.getOperatingStartTime());
                existMenu.setOperatingEndTime(mealRequestDTO.getOperatingEndTime());
                existMenu.setMainMenu(mealRequestDTO.getMainMenu());
                existMenu.setSubMenu(mealRequestDTO.getSubMenu());
                existMenu.setWeekStartDate(LocalDate.parse(weekStartDate));
                existMenu.setMenuStatus(menuStatus);
                return existMenu;
            }).orElseGet(() -> Menu.builder()
                    .dayOfWeek(dayOfWeek)
                    .mealType(mealRequestDTO.getMealTypeEnum())
                    .menuStatus(menuStatus)
                    .operatingStartTime(mealRequestDTO.getOperatingStartTime())
                    .operatingEndTime(mealRequestDTO.getOperatingEndTime())
                    .mainMenu(mealRequestDTO.getMainMenu())
                    .subMenu(mealRequestDTO.getSubMenu())
                    .weekStartDate(LocalDate.parse(weekStartDate))
                    .restaurant(restaurant)
                    .build());

            menuRepository.save(menu);
        }
    }

    public List<Menu> getDailyMealsByMenuStatus(RestaurantName restaurantName, LocalDate weekStartDate, DayOfWeek dayOfWeek, MenuStatus menuStatus) {

        return menuRepository.getDailyMealByMenuStatus(
                restaurantName,
                weekStartDate,
                dayOfWeek,
                menuStatus
        );
    }

    public List<Menu> getWeeklyMealsByMenuStatus(RestaurantName restaurantName, LocalDate weekStartDate, MenuStatus menuStatus) {

        return menuRepository.getWeeklyMealByMenuStatus(
                restaurantName,
                weekStartDate,
                menuStatus
        );
    }
}
