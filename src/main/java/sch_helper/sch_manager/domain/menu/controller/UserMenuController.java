package sch_helper.sch_manager.domain.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch_helper.sch_manager.domain.menu.dto.ApprovedDetailRequest;
import sch_helper.sch_manager.domain.menu.dto.ApprovedTodayRequest;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.service.UserMenuService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserMenuController {

    private final UserMenuService userMenuService;

    @GetMapping("/meal-plans/today")
    public ResponseEntity<?> getApprovedTodayMealPlans(
            @ModelAttribute @Valid ApprovedTodayRequest request
        ) {

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(request.getDayOfWeek());

        return userMenuService.getApprovedTodayMealPlans(dayOfWeek, request.getWeekStartDate());
    }

    @GetMapping("/meal-plans/detail")
    public ResponseEntity<?> getApprovedDetailMealPlans(
            @ModelAttribute @Valid ApprovedDetailRequest request
    ) {

        RestaurantName restaurantName = RestaurantName.valueOf(request.getRestaurantName());

        return userMenuService.getApprovedDetailMealPlans(restaurantName, request.getWeekStartDate());
    }
}
