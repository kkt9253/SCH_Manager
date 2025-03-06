package sch_helper.sch_manager.domain.menu.dto.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sch_helper.sch_manager.domain.menu.dto.base.RestaurantResponseDTO;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;


@Getter
@Setter
@AllArgsConstructor
public class RestaurantConverter {

    public static RestaurantResponseDTO toResponse(Restaurant restaurant) {
        return RestaurantResponseDTO.builder()
                .restaurantName(restaurant.getName().name())
                .operatingStartTime(restaurant.getOperatingStartTime())
                .operatingEndTime(restaurant.getOperatingEndTime())
                .isActive(restaurant.isActive())
                .build();

    }

}
