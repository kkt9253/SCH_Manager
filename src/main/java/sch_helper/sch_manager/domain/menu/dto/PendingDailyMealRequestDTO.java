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
public class PendingDailyMealRequestDTO {

    @NotBlank
    @Pattern(regexp = "^(HYANGSEOL1|FACULTY)$", message = "유효한 식당을 입력해야 합니다.")
    private String restaurantName;

    @NotBlank(message = "dayOfWeek은 필수 값입니다.")
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY)$", message = "유효한 요일을 입력해야 합니다.")
    private String dayOfWeek;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;
}
