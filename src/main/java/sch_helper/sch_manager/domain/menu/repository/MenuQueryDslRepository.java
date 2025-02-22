package sch_helper.sch_manager.domain.menu.repository;

import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

import java.util.List;

public interface MenuQueryDslRepository {

    List<Menu> getDailyMealByMenuStatus(String restaurantName, DayOfWeek dayOfWeek, MenuStatus menuStatus);
}
