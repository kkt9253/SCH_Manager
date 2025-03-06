package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.DailyMenu;
import sch_helper.sch_manager.domain.menu.entity.WeeklyMenu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.Optional;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long> {

    Optional<DailyMenu> findByWeeklyMenuAndDayOfWeek(WeeklyMenu weeklyMenu, DayOfWeek dayOfWeek);
}
