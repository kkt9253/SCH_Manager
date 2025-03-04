package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

@Entity
public class DailyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_menu_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "daily_image", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] dailyImage;

    @ManyToOne
    @JoinColumn(name = "weekly_menu_id", nullable = false)
    private WeeklyMenu weeklyMenu;
}
