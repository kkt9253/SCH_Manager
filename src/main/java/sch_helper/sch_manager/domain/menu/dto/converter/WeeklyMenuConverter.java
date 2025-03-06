package sch_helper.sch_manager.domain.menu.dto.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.domain.menu.dto.base.WeeklyMenuResponse;
import sch_helper.sch_manager.domain.menu.entity.WeeklyMenu;


@Component
@RequiredArgsConstructor
public class WeeklyMenuConverter {

    private final DailyMenuConverter dailyMenuConverter;
    private final FileUtil fileUtil;

    @Transactional(readOnly = true)
    public WeeklyMenuResponse toResponse(WeeklyMenu weeklyMenu) {

        // weekly 의 이미지가 있으면 있는거 주고 없으면 null 반환
        byte[] nullableBase64Image = weeklyMenu.getWeeklyImage() == null ? null : fileUtil.encodeByteToBase64(weeklyMenu.getWeeklyImage());

        return WeeklyMenuResponse.builder()
                .weekStartDate(String.valueOf(weeklyMenu.getWeekStartDate()))
                .weekEndDate(String.valueOf(weeklyMenu.getWeekEndDate()))
                .dailyMenuResponseList(
                        weeklyMenu.getDailyMenuList().stream().map(dailyMenuConverter::toResponse).toList()
                )
                .weeklyMenuImg(nullableBase64Image)
                .build();
    }

}
