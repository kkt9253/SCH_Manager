package sch_helper.sch_manager.domain.menu.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.List;


@Getter
@NoArgsConstructor
public class DailyMealRequestDTO {

    @NotBlank(message = "dayOfWeek은 필수 값입니다.")
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY)$", message = "유효한 요일을 입력해야 합니다.")
    private String dayOfWeek;

    @JsonProperty("meals")
    @NotNull(message = "meals 리스트는 최소 1개 이상 필요합니다.")
    @Size(min = 1, message = "meals 리스트는 최소 1개 이상이어야 합니다.")
    private List<@Valid MealRequestDTO> meals;

//    public DayOfWeek getDayOfWeekEnum() {
//        return DayOfWeek.valueOf(this.dayOfWeek);
//    }
}
