package sch_helper.sch_manager.domain.menu.dto.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMenuResponse;
import sch_helper.sch_manager.domain.menu.entity.DailyMenu;
import sch_helper.sch_manager.domain.menu.entity.WeeklyMenu;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DailyMenuConverter {

    private final MealConverter mealConverter;
    private final FileUtil fileUtil;

    @Transactional(readOnly = true)
    public DailyMenuResponse toResponse(DailyMenu dailyMenu) {

        byte[] nullableBase64Image = dailyMenu.getDailyImage() == null ? null : fileUtil.encodeByteToBase64(dailyMenu.getDailyImage());

        return DailyMenuResponse.builder()
                .dayOfWeek(dailyMenu.getDayOfWeek())
                .mealResponseDTOList(
                        dailyMenu.getMealList().stream().map(mealConverter::toResponse).toList()
                )
                .dailyMenuImg(nullableBase64Image)
                .build();
    }

    public DailyMenu toEntity(DailyMealRequestDTO request, WeeklyMenu weeklyMenu) {
        return DailyMenu.builder()
                .dayOfWeek(DayOfWeek.valueOf(request.getDayOfWeek()))
                .dailyImage(null)
                .weeklyMenu(weeklyMenu)
                .mealList(new ArrayList<>())
                .build();
    }

    public DailyMenu toEntityWithImage(DailyMealRequestDTO request, MultipartFile imageFile, WeeklyMenu weeklyMenu) {
        return DailyMenu.builder()
                .dayOfWeek(DayOfWeek.valueOf(request.getDayOfWeek()))
                .dailyImage(fileUtil.imageToByte(imageFile))
                .weeklyMenu(weeklyMenu)
                .mealList(new ArrayList<>())
                .build();
    }

}
