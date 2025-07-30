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
import sch_helper.sch_manager.domain.menu.entity.MenuImage;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.repository.MenuImageRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterMenuService {

    private final RestaurantRepository restaurantRepository;
    private final MenuImageRepository menuImageRepository;
    private final MenuUtil menuUtil;
    private final FileUtil fileUtil;

    public ResponseEntity<?> uploadDailyMealPlans(
            String restaurantName,
            String weekStartDate,
            DailyMealRequestDTO dailyMealRequestDTO
    ) {

        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 요청에 대한 이미지가 존재하지 않는다면 관리자로부터 아직 업로드가 되지 않은 것이기 때문에 예외 발생
        String weekImageName = restaurantName + "-" + weekStartDate + "-week";
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

        String dayImageName = restaurantName + "-" + weekStartDate + "-" + dailyMealRequestDTO.getDayOfWeek();
        MenuImage dayMenuImage = menuImageRepository.findByImageName(dayImageName)
                .orElse(null);
        byte[] encodeDayMealImage = null;
        if (dayMenuImage != null) {
            encodeDayMealImage = fileUtil.encodeByteToBase64(dayMenuImage.getImageBinary());
        }

        // 마스터가 최종 업로드한 식단표 처리
        menuUtil.saveDailyMeal(restaurant, weekStartDate, dailyMealRequestDTO, MenuStatus.APPROVED);
        menuUtil.saveDailyMeal(restaurant, weekStartDate, dailyMealRequestDTO, MenuStatus.PENDING);

        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                RestaurantName.valueOf(restaurantName),
                LocalDate.parse(weekStartDate),
                DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );
        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                encodeDayMealImage,
                encodeWeekMealImage,
                new DailyMealResponseDTO(dailyMealRequestDTO.getDayOfWeek(), MealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Daily meal plans Final approval and uploaded successfully.",
                pendingDailyMealResponseDTO
        ));
    }

    public ResponseEntity<?> getPendingWeeklyMealPlans(PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO) {

        String weekImageName = pendingWeeklyMealRequestDTO.getRestaurantName() + "-" + pendingWeeklyMealRequestDTO.getWeekStartDate().toString() + "-week";
        MenuImage menuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        byte[] weekMealImage = fileUtil.encodeByteToBase64(menuImage.getImageBinary());

        List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                RestaurantName.valueOf(pendingWeeklyMealRequestDTO.getRestaurantName()),
                pendingWeeklyMealRequestDTO.getWeekStartDate(),
                MenuStatus.PENDING
        );

        if (menus.isEmpty()) {
            List<DailyMealResponseDTO> emptyDailyMeals = Arrays.stream(DayOfWeek.values())
                    .map(day -> new DailyMealResponseDTO(day.name(), Collections.emptyList()))
                    .collect(Collectors.toList());

            PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                    weekMealImage,
                    emptyDailyMeals
            );

            return ResponseEntity.ok(SuccessResponse.ok(pendingWeeklyMealResponseDTO));
        }

        List<DailyMealResponseDTO> dailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);

        PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                weekMealImage,
                dailyMealResponseDTOs
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingWeeklyMealResponseDTO));
    }

    public ResponseEntity<?> getPendingDailyMealPlans(PendingDailyMealRequestDTO pendingDailyMealRequestDTO) {

        String weekImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-week";
        // 일주일치 이미지는 무조건 존재해야 함
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

        String dayImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-" + pendingDailyMealRequestDTO.getDayOfWeek();
        // 특정 요일 이미지는 존재하지 않을 수도 있음 (일주일 식단 업로드 시에 Menu는 저장되지만, 이때 특정 요일 이미지는 없기 때문)
        MenuImage dayMenuImage = menuImageRepository.findByImageName(dayImageName)
                .orElse(null);

        byte[] encodeDayMealImage = null;
        if (dayMenuImage != null) {
            encodeDayMealImage = fileUtil.encodeByteToBase64(dayMenuImage.getImageBinary());
        }

        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                RestaurantName.valueOf(pendingDailyMealRequestDTO.getRestaurantName()),
                pendingDailyMealRequestDTO.getWeekStartDate(),
                DayOfWeek.valueOf(pendingDailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );

        if (menus.isEmpty()) {

            DailyMealResponseDTO emptyDailyMeal = new DailyMealResponseDTO(pendingDailyMealRequestDTO.getDayOfWeek(), Collections.emptyList());

            PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                    encodeDayMealImage,
                    encodeWeekMealImage,
                    emptyDailyMeal
            );

            return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
        }

        List<MealResponseDTO> mealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                encodeDayMealImage,
                encodeWeekMealImage,
                new DailyMealResponseDTO(pendingDailyMealRequestDTO.getDayOfWeek(), mealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
    }
//
//    public ResponseEntity<?> getPendingWeeklyMealPlans(PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO) {
//
//        List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
//                RestaurantName.valueOf(pendingWeeklyMealRequestDTO.getRestaurantName()),
//                pendingWeeklyMealRequestDTO.getWeekStartDate(),
//                MenuStatus.PENDING
//        );
//        if (menus.isEmpty()) {
//
//            return ResponseEntity.ok(SuccessResponse.ok("식단표가 아직 업로드되지 않았습니다."));
//        }
//        List<DailyMealResponseDTO> DailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);
//
//        String weekImageName = pendingWeeklyMealRequestDTO.getRestaurantName() + "-" + pendingWeeklyMealRequestDTO.getWeekStartDate().toString() + "-week";
//        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
//                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
//
//        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());
//
//        PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
//                encodeWeekMealImage,
//                DailyMealResponseDTOs
//        );
//
//        return ResponseEntity.ok(SuccessResponse.ok(pendingWeeklyMealResponseDTO));
//    }
//
//    public ResponseEntity<?> getPendingDailyMealPlans(PendingDailyMealRequestDTO pendingDailyMealRequestDTO) {
//
//        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
//                RestaurantName.valueOf(pendingDailyMealRequestDTO.getRestaurantName()),
//                pendingDailyMealRequestDTO.getWeekStartDate(),
//                DayOfWeek.valueOf(pendingDailyMealRequestDTO.getDayOfWeek()),
//                MenuStatus.PENDING
//        );
//        if (menus.isEmpty()) {
//            return ResponseEntity.ok(SuccessResponse.ok("식단표가 아직 업로드되지 않았습니다."));
//        }
//
//        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);
//
//        String dayImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-" + pendingDailyMealRequestDTO.getDayOfWeek();
//        // 특정 요일 이미지는 존재하지 않을 수도 있음 (일주일 식단 업로드 시에 Menu는 저장되지만, 이때 특정 요일 이미지는 없기 때문)
//        MenuImage dayMenuImage = menuImageRepository.findByImageName(dayImageName)
//                .orElse(null);
//
//        byte[] encodeDayMealImage = null;
//        if (dayMenuImage != null) {
//            encodeDayMealImage = fileUtil.encodeByteToBase64(dayMenuImage.getImageBinary());
//        }
//
//        String weekImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-week";
//        // 일주일치 이미지는 무조건 존재해야 함
//        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
//                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));
//        byte[] encodeWeekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());
//
//        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
//                encodeDayMealImage,
//                encodeWeekMealImage,
//                new DailyMealResponseDTO(pendingDailyMealRequestDTO.getDayOfWeek(), MealResponseDTOs)
//        );
//
//        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
//    }
}
