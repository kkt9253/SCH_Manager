package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(name = "restaurant_name", nullable = false, unique = true)
    private RestaurantName name;

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

    // 고정 운영시간 변경을 위한 메소드
    public void changeOperatingStartTime(String operatingStartTime) {
        this.operatingStartTime = operatingStartTime;
    }

    public void changeOperatingEndTime(String operatingEndTime) {
        this.operatingEndTime = operatingEndTime;
    }
}