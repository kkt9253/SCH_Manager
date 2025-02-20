package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealDTO;
import sch_helper.sch_manager.domain.menu.service.MasterService;

import java.time.DayOfWeek;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master")
public class MasterController {

    private final MasterService masterService;
    private final DateUtil dateUtil;

    @PostMapping("/meal-plans/{restaurant-name}")
    public ResponseEntity<?> uploadDailyMealPlans(
            @PathVariable(name = "restaurant-name") String restaurantName,
            @RequestPart("weekStartDate") String weekStartDate,
            @RequestPart(value = "dailyMeals") @Valid DailyMealDTO dailyMealDTO
    ) {

        System.out.println("restaurantName: " + restaurantName);
        System.out.println("weekStartDate: " + weekStartDate);
        System.out.println("dailyMealDTO: " + dailyMealDTO);

        if (!dateUtil.isSameDayOfWeek(weekStartDate, DayOfWeek.MONDAY)) {
            throw new ApiException(ErrorCode.DATE_DAY_MISMATCH);
        }

        return masterService.uploadMasterDailyMealPlans(restaurantName, dailyMealDTO);
    }
}
