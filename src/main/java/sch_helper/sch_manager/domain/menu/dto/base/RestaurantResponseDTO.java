package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {

    private String restaurantName;
    private String operatingStartTime;
    private String operatingEndTime;
    private boolean isActive;
}
