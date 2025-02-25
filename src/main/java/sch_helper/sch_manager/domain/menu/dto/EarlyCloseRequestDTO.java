package sch_helper.sch_manager.domain.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EarlyCloseRequestDTO {

    // mealType 은 필요 없으므로 넣지 않음

    @NotBlank
    private boolean earlyClose;
}
