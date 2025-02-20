package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final DateUtil dateUtil;

    @PostMapping("/week-meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadWeeklyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid List<DailyMealDTO> dailyMealDTOS,
            @RequestPart("weeklyMealImg") MultipartFile weeklyMealImg
            ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTOS: " + dailyMealDTOS);
        System.out.println("weeklyMealImg: " + weeklyMealImg);

        // 폼 형식 안맞췄을 때 예외처리 필요
        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        // 식당과 사용자의 권한을 비교하여 동일하지 않으면 예외처리
        return adminService.uploadAdminWeeklyMealPlans(restaurantName, weekStartDate, dailyMealDTOS, weeklyMealImg);
    }

    @PostMapping("/meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals", required = false) @Valid DailyMealDTO dailyMealDTO,
            @RequestPart("dailyMealImg") MultipartFile dailyMealImg
    ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTO: " + dailyMealDTO);

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return adminService.uploadAdminDailyMealPlans(restaurantName, weekStartDate, dailyMealDTO, dailyMealImg);
    }

//    @GetMapping("/meal-plans/{restaurant-name}/{day-of-week}/{week-start-date}")
//    public ResponseEntity<?> getMealPlans(
//            @PathVariable(name = "restaurant-name") String restaurantName,
//            @PathVariable(name = "day-of-week") String dayOfWeek,
//            @PathVariable(name = "week-start-date") String weekStartDate
//    ) {
//
//        System.out.println("restaurantName: " + restaurantName);
//        System.out.println("dayOfWeek: " + dayOfWeek);
//
//        return adminService.getAdminDayMealPlans();
//    }
}
