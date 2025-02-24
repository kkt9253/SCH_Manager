package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final FileUtil fileUtil;
    private final MenuUtil menuUtil;

    public ResponseEntity<?> uploadWeeklyMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealRequestDTO> dailyMealRequestDTOS,
            MultipartFile weeklyMealImg
    ) {

        String savedImgPath = null;
        try {

            String fileName = restaurantName + "-" + weekStartDate + "-week.jpg";
            savedImgPath = fileUtil.saveFile(weeklyMealImg, "weeklyMealImg", fileName);

            Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (dailyMealRequestDTOS != null) {
                for (DailyMealRequestDTO dailyMealRequestDTO : dailyMealRequestDTOS) {

                    menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
                }
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        // get에서 했듯이 쿼리문으로 가져온 menu 정보를 반환할지, 사용자로부터 받은 데이터로부터 반환할지
        // 쿼리문으로 사용 안한 이유는 성능상 아래처럼 하는게 더 빠르다고 생각해서 (물론 사용자 얼마 없어서 성능에 의미 없을거 같긴 함)
        List<DailyMealResponseDTO> dailyMealResponseDTOS = dailyMealRequestDTOS.stream()
                .map(dailyMealRequestDTO -> new DailyMealResponseDTO(
                        dailyMealRequestDTO.getDayOfWeek(),
                        dailyMealRequestDTO.getMeals().stream()
                                .map(mealRequestDTO -> new MealResponseDTO(
                                        mealRequestDTO.getMealType(),
                                        mealRequestDTO.getOperatingStartTime(),
                                        mealRequestDTO.getOperatingEndTime(),
                                        mealRequestDTO.getMainMenu(),
                                        mealRequestDTO.getSubMenu()
                                ))
                                .toList()
                ))
                .toList();

        PendingWeeklyMealResponseDTO response = new PendingWeeklyMealResponseDTO(
                savedImgPath,
                dailyMealResponseDTOS
        );

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Weekly meal plans uploaded successfully.",
                response
        ));
    }


    public ResponseEntity<?> uploadDailyMealPlans(
            String restaurantName,
            String weekStartDate,
            DailyMealRequestDTO dailyMealRequestDTO,
            MultipartFile dailyMealImg
    ) {

        String savedImgPath = null;
        try {

            String fileName = restaurantName + "-" + weekStartDate + "-day.jpg";
            savedImgPath = fileUtil.saveFile(dailyMealImg, "dailyMealImg", fileName);

            Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (dailyMealRequestDTO != null) {

                menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        PendingDailyMealResponseDTO response = new PendingDailyMealResponseDTO(
                savedImgPath,
                null,
                new DailyMealResponseDTO(
                        dailyMealRequestDTO.getDayOfWeek(),
                        dailyMealRequestDTO.getMeals().stream()
                                .map(mealRequestDTO -> new MealResponseDTO(
                                        mealRequestDTO.getMealType(),
                                        mealRequestDTO.getOperatingStartTime(),
                                        mealRequestDTO.getOperatingEndTime(),
                                        mealRequestDTO.getMainMenu(),
                                        mealRequestDTO.getSubMenu()
                                ))
                                .toList()
                )
        );

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Daily meal plans uploaded successfully.",
                response
        ));
    }


    public ResponseEntity<?> getPendingDailyMealPlans(PendingDailyMealRequestDTO pendingDailyMealRequestDTO) {

        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                pendingDailyMealRequestDTO.getRestaurantName(),
                DayOfWeek.valueOf(pendingDailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );
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


    public ResponseEntity<?> getPendingWeeklyMealPlans(PendingWeeklyMealRequestDTO pendingWeeklyMealRequestDTO) {

        List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                pendingWeeklyMealRequestDTO.getRestaurantName(),
                MenuStatus.PENDING
        );
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
}

/*
[
        "weekStartDate": "2025-02-17",
        {
            "dayOfWeek": "MONDAY",
            "meals": [
                {
                    "mealType": "BREAKFAST",
                    "operatingStartTime": "08:00",
                    "operatingEndTime": "09:30",
                    "mainMenu": "된장찌개, 비빔밥, 도시락",
                    "subMenu": "김치, 감자볶음"
                },
                {
                    "mealType": "LUNCH",
                    "operatingStartTime": "11:00",
                    "operatingEndTime": "14:00",
                    "mainMenu": "불고기, 된장찌개",
                    "subMenu": "샐러드, 계란찜"
                }
            ]
        },
        {
            "dayOfWeek": "TUESDAY",
            "meals": [
                {
                    "mealType": "BREAKFAST",
                    "operatingStartTime": "08:00",
                    "operatingEndTime": "09:30",
                    "mainMenu": "된장찌개2, 비빔밥2, 도시락2",
                    "subMenu": "김치2, 감자볶음2"
                },
                {
                    "mealType": "LUNCH",
                    "operatingStartTime": "11:00",
                    "operatingEndTime": "14:00",
                    "mainMenu": "불고기2, 된장찌개2",
                    "subMenu": "샐러드2, 계란찜2"
                }
            ]
        }
]
*/