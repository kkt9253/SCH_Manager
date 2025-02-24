package sch_helper.sch_manager.domain.menu.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.QMenu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuQueryDslRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuQueryDslRepositoryImpl implements MenuQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Menu> getDailyMealByMenuStatus(String restaurantName, DayOfWeek dayOfWeek, MenuStatus menuStatus) {

        QMenu menu = QMenu.menu;

        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.restaurant.name.eq(restaurantName),
                        menu.dayOfWeek.eq(dayOfWeek),
                        menu.menuStatus.eq(menuStatus)
                )
                .orderBy(menu.mealType.asc())
                .fetch();
    }

    @Override
    public List<Menu> getWeeklyMealByMenuStatus(String restaurantName, MenuStatus menuStatus) {

        QMenu menu = QMenu.menu;

        return queryFactory
                .selectFrom(menu)
                .where(
                        menu.restaurant.name.eq(restaurantName),
                        menu.menuStatus.eq(menuStatus)
                )
                .orderBy(
                        menu.dayOfWeek.asc(),
                        menu.mealType.asc()
                )
                .fetch();
    }
}
