package sch_helper.sch_manager.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalOperatingTimeRequestDTO {

    @NotBlank
    private String newOperatingStartTime;
    @NotBlank
    private String newOperatingEndTime;
}
