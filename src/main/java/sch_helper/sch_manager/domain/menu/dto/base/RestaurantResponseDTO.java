package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantResponseDTO {

    private String restaurantName;
    private String operatingStartTime;
    private String operatingEndTime;
    private boolean isActive;

    public static RestaurantResponseDTO fromEntity(Restaurant restaurant) {
        return new RestaurantResponseDTO(
                restaurant.getName(),
                restaurant.getOperatingStartTime(),
                restaurant.getOperatingEndTime(),
                restaurant.isActive()
        );
    }

}
