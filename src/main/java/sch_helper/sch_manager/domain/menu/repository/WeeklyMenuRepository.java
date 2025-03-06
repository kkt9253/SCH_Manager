package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.entity.WeeklyMenu;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeeklyMenuRepository extends JpaRepository<WeeklyMenu, Long> {

    Optional<WeeklyMenu> findByRestaurantAndWeekStartDate(Restaurant restaurant, LocalDate weekStartDate);
}
