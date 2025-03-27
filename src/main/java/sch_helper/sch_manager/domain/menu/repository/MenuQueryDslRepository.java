package sch_helper.sch_manager.domain.menu.repository;

import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;

import java.time.LocalDate;
import java.util.List;

public interface MenuQueryDslRepository {

    List<Menu> getDailyMealByMenuStatus(RestaurantName restaurantName, LocalDate weekStartDate, DayOfWeek dayOfWeek, MenuStatus menuStatus);

    List<Menu> getWeeklyMealByMenuStatus(RestaurantName restaurantName, LocalDate weekStartDate, MenuStatus menuStatus);
}
