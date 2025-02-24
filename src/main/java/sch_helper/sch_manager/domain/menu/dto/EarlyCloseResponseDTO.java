package sch_helper.sch_manager.domain.menu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EarlyCloseResponseDTO {

    private String restaurantName;
    private String operatingStartTime;
    private String operatingEndTime;
    private boolean isActive;

}
