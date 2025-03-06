package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import sch_helper.sch_manager.domain.menu.dto.converter.DailyMenuConverter;
import sch_helper.sch_manager.domain.menu.dto.converter.MealConverter;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.entity.*;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.repository.DailyMenuRepository;
import sch_helper.sch_manager.domain.menu.repository.MenuImageRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;
import sch_helper.sch_manager.domain.menu.repository.WeeklyMenuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterMenuService {

    private final WeeklyMenuRepository weeklyMenuRepository;
    private final DailyMenuRepository dailyMenuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuImageRepository menuImageRepository;
    private final MenuUtil menuUtil;
    private final FileUtil fileUtil;
    private final MealConverter mealConverter;
    private final DailyMenuConverter dailyMenuConverter;

    @Transactional
    public ResponseEntity<?> uploadDailyMealPlans(
            String restaurantName,
            String weekStartDate,
            DailyMealRequestDTO dailyMealRequestDTO
    ) {

        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        WeeklyMenu weeklyMenu = weeklyMenuRepository.findByRestaurantAndWeekStartDate(restaurant, LocalDate.parse(weekStartDate))
                .orElseThrow(() -> new ApiException(ErrorCode.WEEKLY_MENU_NOT_FOUND));

        DailyMenu dailyMenu = dailyMenuRepository.findByWeeklyMenuAndDayOfWeek(weeklyMenu, DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()))
                .orElseThrow(() -> new ApiException(ErrorCode.DAILY_MENU_NOT_FOUND));

        List<Meal> existingMeals = dailyMenu.getMealList();

        // 3. 기존 Meal 업데이트 또는 추가
        dailyMealRequestDTO.getMeals().forEach(mealDto -> {
            Optional<Meal> existingMealOpt = existingMeals.stream()
                    .filter(m -> m.getMealType() == MealType.valueOf(mealDto.getMealType()))
                    .findFirst();

            if (existingMealOpt.isPresent()) {
                // 3-1. 기존 Meal 업데이트
                Meal existingMeal = existingMealOpt.get();
                existingMeal.updateFromDto(mealDto); // 엔터티에 업데이트 메서드 추가
            }
        });

        return ResponseEntity.ok(SuccessResponse.ok(dailyMenuConverter.toResponse(dailyMenu)));

    }

    public ResponseEntity<?> getPendingWeeklyMealPlans(PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO) {

        List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                pendingWeeklyMealRequestDTO.getRestaurantName(),
                MenuStatus.PENDING
        );
        if (menus.isEmpty()) {

            return ResponseEntity.ok(SuccessResponse.ok("식단표가 아직 업로드되지 않았습니다."));
        }
        List<DailyMealResponseDTO> DailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);

        String weekImageName = pendingWeeklyMealRequestDTO.getRestaurantName() + "-" + pendingWeeklyMealRequestDTO.getWeekStartDate().toString() + "-week";
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

        PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                encodeWeekMealImage,
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
            return ResponseEntity.ok(SuccessResponse.ok("식단표가 아직 업로드되지 않았습니다."));
        }

        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

        String dayImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-" + pendingDailyMealRequestDTO.getDayOfWeek();
        // 특정 요일 이미지는 존재하지 않을 수도 있음 (일주일 식단 업로드 시에 Menu는 저장되지만, 이때 특정 요일 이미지는 없기 때문)
        MenuImage dayMenuImage = menuImageRepository.findByImageName(dayImageName)
                .orElse(null);

        byte[] encodeDayMealImage = null;
        if (dayMenuImage != null) {
            encodeDayMealImage = fileUtil.encodeByteToBase64(dayMenuImage.getImageBinary());
        }

        String weekImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-week";
        // 일주일치 이미지는 무조건 존재해야 함
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                encodeDayMealImage,
                encodeWeekMealImage,
                new DailyMealResponseDTO(pendingDailyMealRequestDTO.getDayOfWeek(), MealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
    }
}
