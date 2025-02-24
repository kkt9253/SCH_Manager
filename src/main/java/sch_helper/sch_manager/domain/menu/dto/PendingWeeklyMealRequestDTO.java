package sch_helper.sch_manager.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PendingWeeklyMealRequestDTO {

    @NotBlank
    @Pattern(regexp = "^(HYANGSEOL1|FACULTY)$", message = "유효한 식당을 입력해야 합니다.")
    private String restaurantName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;
}
