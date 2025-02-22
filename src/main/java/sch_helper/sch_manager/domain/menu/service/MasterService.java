package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.MenuUtil;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final RestaurantRepository restaurantRepository;
    private final MenuUtil menuUtil;

    public ResponseEntity<?> uploadMasterDailyMealPlans(
            String restaurantName,
            DailyMealRequestDTO dailyMealRequestDTO
    ) {

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 최종 승인이기 때문에 관리자가 조회하는 식단표도 최신화해줘야 함.
        menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.APPROVED);
        menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "daily meal plans Final approval and uploaded successfully."
        ));
    }

}
