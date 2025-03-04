package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

@Entity
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_status", nullable = false)
    private MenuStatus menuStatus;

    @Column(name = "main_menu", nullable = false)
    private String mainMenu;

    @Column(name = "sub_menu")
    private String subMenu;

    @Column(name = "operating_start_time", nullable = false)
    private String operatingStartTime;

    @Column(name = "operating_end_time", nullable = false)
    private String operatingEndTime;

    @ManyToOne
    @JoinColumn(name = "daily_menu_id")
    private DailyMenu dailyMenu;
}
