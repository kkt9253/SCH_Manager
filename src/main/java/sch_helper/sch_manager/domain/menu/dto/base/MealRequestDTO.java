package sch_helper.sch_manager.domain.menu.dto.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch_helper.sch_manager.domain.menu.enums.MealType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MealRequestDTO {

    @NotBlank(message = "mealType은 필수 값입니다.")
    @Pattern(regexp = "^(BREAKFAST|LUNCH|DINNER)$", message = "유효한 식사시간을 입력해야 합니다.")
    private String mealType;

    @NotBlank(message = "operatingStartTime은 필수 값입니다.")
    private String operatingStartTime;

    @NotBlank(message = "operatingEndTime은 필수 값입니다.")
    private String operatingEndTime;

    @NotBlank(message = "mainMenu는 필수 값입니다.")
    private String mainMenu;

    // 식당이 쉬는 날같은 상황에선 mainMenu에만 "운영하지 않습니다."를 입력하고, subMenu엔 입력할 필요가 없기 때문에 제거하는게 나을듯
    // @NotBlank(message = "subMenu는 필수 값입니다.")
    private String subMenu;

    public MealType getMealTypeEnum() {
        return MealType.valueOf(this.mealType);
    }
}
