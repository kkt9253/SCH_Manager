package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.MenuUtil;
import sch_helper.sch_manager.domain.menu.dto.ApprovedDetailMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.ApprovedTodayMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMenuService {

    private final MenuUtil menuUtil;
    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> getApprovedTodayMealPlans(
        DayOfWeek dayOfWeek, LocalDate weekStartDate
    ) {

        List<ApprovedTodayMealResponseDTO> approvedTodayMealResponseDTOs = new ArrayList<>();

        for (RestaurantName restaurant : RestaurantName.values()) {

            Restaurant restaurantEntity = restaurantRepository.findByName(restaurant)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            boolean isActive = restaurantEntity.isActive();

            List<MealResponseDTO> mealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(
                    menuUtil.getDailyMealsByMenuStatus(
                            RestaurantName.valueOf(restaurant.name()),
                            weekStartDate,
                            dayOfWeek,
                            MenuStatus.APPROVED)
            );

            DailyMealResponseDTO dailyMealResponseDTO = new DailyMealResponseDTO(dayOfWeek.name(), mealResponseDTOs);

            ApprovedTodayMealResponseDTO approvedTodayMealResponseDTO = new ApprovedTodayMealResponseDTO(restaurant.name(), isActive, dailyMealResponseDTO);

            approvedTodayMealResponseDTOs.add(approvedTodayMealResponseDTO);
        }

        return ResponseEntity.ok(SuccessResponse.ok(approvedTodayMealResponseDTOs));
    }

    public ResponseEntity<?> getApprovedDetailMealPlans(
            RestaurantName restaurantName, LocalDate weekStartDate
    ) {

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        String restaurantOperatingStartTime = restaurant.getOperatingStartTime();
        String restaurantOperatingEndTime = restaurant.getOperatingEndTime();
        boolean isActive = restaurant.isActive();

        List<DailyMealResponseDTO> dailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(
                menuUtil.getWeeklyMealsByMenuStatus(restaurantName, weekStartDate, MenuStatus.APPROVED));

        return ResponseEntity.ok(SuccessResponse.ok(new ApprovedDetailMealResponseDTO(
                restaurantOperatingStartTime,
                restaurantOperatingEndTime,
                isActive,
                dailyMealResponseDTOs)
        ));
    }

}
