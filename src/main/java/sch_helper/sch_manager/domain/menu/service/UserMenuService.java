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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMenuService {

    private final MenuUtil menuUtil;
    private final RestaurantRepository restaurantRepository;

    public ResponseEntity<?> getApprovedTodayMealPlans(String dayOfWeek) {

        List<ApprovedTodayMealResponseDTO> approvedTodayMealResponseDTOs = new ArrayList<>();

        for (RestaurantName restaurant : RestaurantName.values()) {

            Restaurant restaurantEntity = restaurantRepository.findByName(restaurant.name())
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            String restaurantName = restaurantEntity.getName();
            boolean isActive = restaurantEntity.isActive();

            List<MealResponseDTO> mealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(
                    menuUtil.getDailyMealsByMenuStatus(
                            restaurantName,
                            DayOfWeek.valueOf(dayOfWeek),
                            MenuStatus.APPROVED)
            );

            DailyMealResponseDTO dailyMealResponseDTO = new DailyMealResponseDTO(dayOfWeek, mealResponseDTOs);

            ApprovedTodayMealResponseDTO approvedTodayMealResponseDTO = new ApprovedTodayMealResponseDTO(restaurantName, isActive, dailyMealResponseDTO);

            approvedTodayMealResponseDTOs.add(approvedTodayMealResponseDTO);
        }

        return ResponseEntity.ok(SuccessResponse.ok(approvedTodayMealResponseDTOs));
    }

    public ResponseEntity<?> getApprovedDetailMealPlans(String restaurantName) {

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        String restaurantOperatingStartTime = restaurant.getOperatingStartTime();
        String restaurantOperatingEndTime = restaurant.getOperatingEndTime();
        boolean isActive = restaurant.isActive();

        List<DailyMealResponseDTO> dailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(
                menuUtil.getWeeklyMealsByMenuStatus(restaurantName, MenuStatus.APPROVED));

        return ResponseEntity.ok(SuccessResponse.ok(new ApprovedDetailMealResponseDTO(
                restaurantOperatingStartTime,
                restaurantOperatingEndTime,
                isActive,
                dailyMealResponseDTOs)
        ));
    }

}
