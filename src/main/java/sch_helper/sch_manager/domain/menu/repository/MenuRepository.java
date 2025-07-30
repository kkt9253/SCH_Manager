package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, MenuQueryDslRepository {

    Optional<Menu> findByRestaurantIdAndWeekStartDateAndDayOfWeekAndMealTypeAndMenuStatus(
            Long restaurantId, LocalDate weekStartDate, DayOfWeek dayOfWeek, MealType mealType, MenuStatus menuStatus
    );
}