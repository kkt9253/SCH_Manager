package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_status", nullable = false)
    private MenuStatus menuStatus;

    @Column(name = "operating_start_time", nullable = false)
    private String operatingStartTime;

    @Column(name = "operating_end_time", nullable = false)
    private String operatingEndTime;

    @Column(name = "main_menu", nullable = false)
    private String mainMenu;

    @Column(name = "sub_menu")
    private String subMenu;

    @Column(name = "unique_key", nullable = false, unique = true)
    private String unique;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}
