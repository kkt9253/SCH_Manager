package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.common.util.MenuUtil;
import sch_helper.sch_manager.domain.menu.dto.PendingDailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingDailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingWeeklyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.PendingWeeklyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterMenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuUtil menuUtil;
    private final FileUtil fileUtil;

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

    public ResponseEntity<?> getPendingWeeklyMealPlans(PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO) {

        List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                pendingWeeklyMealRequestDTO.getRestaurantName(),
                MenuStatus.PENDING
        );
        if (menus.isEmpty()) {
            throw new ApiException(ErrorCode.MENU_NOT_FOUND);
        }
        List<DailyMealResponseDTO> DailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);


        String restaurantName = pendingWeeklyMealRequestDTO.getRestaurantName();
        String weekStartDate = pendingWeeklyMealRequestDTO.getWeekStartDate().toString();

        String weekFileName = restaurantName + "-" + weekStartDate + "-week.jpg";
        String weekMealImgPath = fileUtil.getFile("weeklyMealImg", weekFileName);


        // 이미지 경로가 아닌 이미지 보내는 걸로 수정 예정
        PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                weekMealImgPath,
                DailyMealResponseDTOs
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingWeeklyMealResponseDTO));
    }

    public ResponseEntity<?> getPendingDailyMealPlans(PendingDailyMealRequestDTO pendingDailyMealRequestDTO) {

        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                pendingDailyMealRequestDTO.getRestaurantName(),
                DayOfWeek.valueOf(pendingDailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );
        if (menus.isEmpty()) {
            throw new ApiException(ErrorCode.MENU_NOT_FOUND);
        }
        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);


        String dayOfWeek = pendingDailyMealRequestDTO.getDayOfWeek();
        String restaurantName = pendingDailyMealRequestDTO.getRestaurantName();
        String weekStartDate = pendingDailyMealRequestDTO.getWeekStartDate().toString();

        String dayFileName = restaurantName + "-" + weekStartDate + "-day.jpg";
        String weekFileName = restaurantName + "-" + weekStartDate + "-week.jpg";

        String dayMealImgPath = fileUtil.getFile("dailyMealImg", dayFileName);
        String weekMealImgPath = fileUtil.getFile("weeklyMealImg", weekFileName);


        // 이미지 경로가 아닌 이미지 보내는 걸로 수정 예정
        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                dayMealImgPath,
                weekMealImgPath,
                new DailyMealResponseDTO(dayOfWeek, MealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
    }
}
