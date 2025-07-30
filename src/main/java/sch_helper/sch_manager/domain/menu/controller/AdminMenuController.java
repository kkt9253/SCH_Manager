package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.domain.menu.dto.EarlyCloseRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingDailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingWeeklyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.TotalOperatingTimeRequestDTO;
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

    @PostMapping("/meal-plans/{restaurant-name}/{day-of-week}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @PathVariable(name = "day-of-week") String dayOfWeek, // 특정 요일 확실하게 알려면 필요함. 아래에 중복되긴 하는데 dto 여러 곳에서 사용해서 냅둬야 할 듯
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid DailyMealRequestDTO dailyMealRequestDTO,
            @RequestPart("dailyMealImg") MultipartFile dailyMealImg
    ) {

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }
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

        return adminMenuService.uploadDailyMealPlans(restaurantName, dayOfWeek, weekStartDate, dailyMealRequestDTO, dailyMealImg);
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

    // 조기마감하기
    @PostMapping("/early-close/{restaurant-name}")
    public ResponseEntity<?> earlyClose(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestBody @Valid EarlyCloseRequestDTO earlyCloseRequestDTO
    ) {
        return adminMenuService.earlyClose(restaurantName, earlyCloseRequestDTO);
    }

    // 고정 운영시간 변경
    @PostMapping("/total-operating-time/{restaurant-name}")
    public ResponseEntity<?> updateTotalOperatingTime(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestBody @Valid TotalOperatingTimeRequestDTO request
    ) {
        return adminMenuService.updateTotalOperatingTime(restaurantName, request);
    }
}
