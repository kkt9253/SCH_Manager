package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.DailyMenu;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long> {
}
