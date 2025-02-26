package sch_helper.sch_manager.domain.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.service.UserMenuService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserMenuController {

    private final UserMenuService userMenuService;

    @GetMapping("/meal-plans/today/{day-of-week}")
    public ResponseEntity<?> getApprovedTodayMealPlans(@PathVariable("day-of-week") String dayOfWeek) {

        if (dayOfWeek.isEmpty() &&
                (
                        !dayOfWeek.equals(DayOfWeek.MONDAY.toString()) ||
                        !dayOfWeek.equals(DayOfWeek.TUESDAY.toString()) ||
                        !dayOfWeek.equals(DayOfWeek.WEDNESDAY.toString()) ||
                        !dayOfWeek.equals(DayOfWeek.THURSDAY.toString()) ||
                        !dayOfWeek.equals(DayOfWeek.FRIDAY.toString())
                )
        ) {
            throw new ApiException(ErrorCode.INVALID_REQUEST_DATA);
        }

        return userMenuService.getApprovedTodayMealPlans(dayOfWeek);
    }


}
