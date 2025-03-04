package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
public class WeeklyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_menu_id")
    private Long id;

    @Column(name = "week_start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate weekEndDate;

    @Column(name = "weekly_image", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] weeklyImage;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}
