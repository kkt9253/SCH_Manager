package sch_helper.sch_manager.domain.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EarlyCloseRequestDTO {

    @NotNull(message = "earlyClose는 필수값입니다.")
    private Boolean earlyClose;
}
