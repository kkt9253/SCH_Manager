package sch_helper.sch_manager.domain.menu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalOperatingTimeRequestDTO {

    private String newOperatingStartTime;
    private String newOperatingEndTime;
}
