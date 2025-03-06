package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WeeklyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_menu_id")
    private Long id;

    @Column(name = "week_start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekEndDate;

    @Column(name = "weekly_image", columnDefinition = "LONGBLOB")
    private byte[] weeklyImage;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    /**
     * dailyMenuList (월, 화, 수, 목, 금)와 양방향 매핑 설정
     * dailyMenu 에는 fetch lazy 적용
     */
    @OneToMany(mappedBy = "weeklyMenu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DailyMenu> dailyMenuList = new ArrayList<>();
}
