package sch_helper.sch_manager.domain.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealDTO;
import sch_helper.sch_manager.domain.menu.service.AdminService;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;
    private final DateUtil dateUtil;

    @PostMapping("/admin1/week-meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadAdmin1WeekMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) List<DailyMealDTO> dailyMealDTOS,
            @RequestPart("weeklyMealImg") MultipartFile weeklyMealImg
            ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTOS: " + dailyMealDTOS);
        System.out.println("weeklyMealImg: " + weeklyMealImg);

        // 폼 형식 안맞췄을 때 예외처리 필요
        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            System.out.println("exex1");
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminService.uploadAdmin1WeekMealPlans(restaurantName, weekStartDate, dailyMealDTOS, weeklyMealImg);
    }
}
