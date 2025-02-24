package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.domain.menu.dto.PendingDailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingWeeklyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.service.MasterMenuService;

import java.time.DayOfWeek;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master")
public class MasterMenuController {

    private final MasterMenuService masterMenuService;
    private final DateUtil dateUtil;

    @PostMapping("/meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals") @Valid DailyMealRequestDTO dailyMealRequestDTO
    ) {

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return masterMenuService.uploadMasterDailyMealPlans(restaurantName, dailyMealRequestDTO);
    }

    @GetMapping("/week-meal-plans")
    public ResponseEntity<?> getPendingWeeklyMealPlans(
            @ModelAttribute @Valid PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO
    ) {

        if (!dateUtil.isSameDayOfWeek(pendingWeeklyMealRequestDTO.getWeekStartDate().toString(), DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return masterMenuService.getPendingWeeklyMealPlans(pendingWeeklyMealRequestDTO);
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<?> getPendingDailyMealPlans(
            @ModelAttribute @Valid PendingDailyMealRequestDTO pendingDailyMealRequestDTO
    ) {

        if (!dateUtil.isSameDayOfWeek(pendingDailyMealRequestDTO.getWeekStartDate().toString(), DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return masterMenuService.getPendingDailyMealPlans(pendingDailyMealRequestDTO);
    }
}
