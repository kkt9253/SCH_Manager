package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(name = "restaurant_name", nullable = false)
    private String name;

    @Column(name = "operating_start_time", nullable = false)
    private String operatingStartTime;

    @Column(name = "operating_end_time", nullable = false)
    private String operatingEndTime;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // 조기 마감을 위한 메소드
    public void changeIsActive(boolean isActive){
        this.isActive = isActive;
    }
}