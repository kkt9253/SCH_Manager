package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.domain.menu.dto.PendingDailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingWeeklyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.service.AdminMenuService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;
    private final DateUtil dateUtil;

    @PostMapping("/week-meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadWeeklyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid List<DailyMealRequestDTO> dailyMealRequestDTOS,
            @RequestPart("weeklyMealImg") MultipartFile weeklyMealImg
            ) {

        // 폼 형식 안맞췄을 때 예외처리 필요
        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        // 식당과 사용자의 권한을 비교하여 동일하지 않으면 예외처리
        return adminMenuService.uploadWeeklyMealPlans(restaurantName, weekStartDate, dailyMealRequestDTOS, weeklyMealImg);
    }

    @PostMapping("/meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid DailyMealRequestDTO dailyMealRequestDTO,
            @RequestPart("dailyMealImg") MultipartFile dailyMealImg
    ) {

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminMenuService.uploadDailyMealPlans(restaurantName, weekStartDate, dailyMealRequestDTO, dailyMealImg);
    }

    @GetMapping("/week-meal-plans")
    public ResponseEntity<?> getPendingWeeklyMealPlans(
            @ModelAttribute @Valid PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO
    ) {

        if (!dateUtil.isSameDayOfWeek(pendingWeeklyMealRequestDTO.getWeekStartDate().toString(), DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminMenuService.getPendingWeeklyMealPlans(pendingWeeklyMealRequestDTO);
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<?> getPendingDailyMealPlans(
            @ModelAttribute @Valid PendingDailyMealRequestDTO pendingDailyMealRequestDTO
            ) {

        if (!dateUtil.isSameDayOfWeek(pendingDailyMealRequestDTO.getWeekStartDate().toString(), DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminMenuService.getPendingDailyMealPlans(pendingDailyMealRequestDTO);
    }
}
