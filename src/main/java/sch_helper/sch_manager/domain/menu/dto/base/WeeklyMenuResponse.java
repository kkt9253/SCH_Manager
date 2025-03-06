package sch_helper.sch_manager.domain.menu.dto.base;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyMenuResponse {

    private String weekStartDate;

    private String weekEndDate;

    private List<DailyMenuResponse> dailyMenuResponseList;

    private byte[] weeklyMenuImg;
}