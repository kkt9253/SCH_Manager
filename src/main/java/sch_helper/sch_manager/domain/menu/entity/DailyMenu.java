package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_menu")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DailyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_menu_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "daily_image", nullable = true, columnDefinition = "LONGBLOB")
    private byte[] dailyImage;

    @ManyToOne
    @JoinColumn(name = "weekly_menu_id", nullable = false)
    private WeeklyMenu weeklyMenu;

    /**
     * !!
     * MealList (조/중/석식)과 양방향 매핑 설정
     * Meal 에는 FetchType.LAZY 적용
     * orphanRemoval = true -> 고아객체를 삭제해줌
     */
    @OneToMany(mappedBy = "dailyMenu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Meal> mealList = new ArrayList<>();
}
