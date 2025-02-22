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
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.service.AdminService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final DateUtil dateUtil;

    @PostMapping("/week-meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadWeeklyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid List<DailyMealRequestDTO> dailyMealRequestDTOS,
            @RequestPart("weeklyMealImg") MultipartFile weeklyMealImg
            ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTOS: " + dailyMealRequestDTOS);
        System.out.println("weeklyMealImg: " + weeklyMealImg);

        // 폼 형식 안맞췄을 때 예외처리 필요
        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        // 식당과 사용자의 권한을 비교하여 동일하지 않으면 예외처리
        return adminService.uploadWeeklyMealPlans(restaurantName, weekStartDate, dailyMealRequestDTOS, weeklyMealImg);
    }

    @PostMapping("/meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid DailyMealRequestDTO dailyMealRequestDTO,
            @RequestPart("dailyMealImg") MultipartFile dailyMealImg
    ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTO: " + dailyMealRequestDTO);

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminService.uploadDailyMealPlans(restaurantName, weekStartDate, dailyMealRequestDTO, dailyMealImg);
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<?> getPendingDailyMealPlans(
            @ModelAttribute @Valid PendingDailyMealRequestDTO pendingDailyMealRequestDTO
            ) {

        System.out.println("pendingDailyMealRequestDTO 1: " + pendingDailyMealRequestDTO.getRestaurantName());
        System.out.println("pendingDailyMealRequestDTO 2: " + pendingDailyMealRequestDTO.getDayOfWeek());
        System.out.println("pendingDailyMealRequestDTO 3: " + pendingDailyMealRequestDTO.getWeekStartDate());

        if (!dateUtil.isSameDayOfWeek(pendingDailyMealRequestDTO.getWeekStartDate().toString(), DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminService.getPendingDailyMealPlans(pendingDailyMealRequestDTO);
    }
}
