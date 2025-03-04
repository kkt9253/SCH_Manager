package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

import java.time.LocalTime;

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
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime operatingStartTime;

    @Column(name = "operating_end_time", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime operatingEndTime;

    @ManyToOne
    @JoinColumn(name = "daily_menu_id")
    private DailyMenu dailyMenu;
}
