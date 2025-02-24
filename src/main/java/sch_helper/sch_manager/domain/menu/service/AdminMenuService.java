package sch_helper.sch_manager.domain.menu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.common.util.MenuUtil;
import sch_helper.sch_manager.domain.menu.dto.*;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.RestaurantResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.dto.converter.RestaurantConverter;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

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
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (dailyMealRequestDTOS != null) {
            for (DailyMealRequestDTO dailyMealRequestDTO : dailyMealRequestDTOS) {

                menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
            }

            List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                    restaurantName,
                    MenuStatus.PENDING
            );
            List<DailyMealResponseDTO> DailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);

            String weekFileName = restaurantName + "-" + weekStartDate + "-week.jpg";
            String weekMealImgPath = fileUtil.getFile("weeklyMealImg", weekFileName);


            PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                    weekMealImgPath,
                    DailyMealResponseDTOs
            );

            return ResponseEntity.ok(SuccessResponse.of(
                    HttpStatus.CREATED,
                    "Weekly meal plans uploaded successfully.",
                    pendingWeeklyMealResponseDTO
            ));
        }

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Weekly meal plans uploaded successfully.",
                savedImgPath
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
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (dailyMealRequestDTO != null) {

            menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);

            List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                    restaurantName,
                    DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                    MenuStatus.PENDING
            );
            List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

            for (MealResponseDTO mealResponseDTO : MealResponseDTOs) {
                System.out.println(mealResponseDTO.getMealType());
            }


            String dayOfWeek = dailyMealRequestDTO.getDayOfWeek();

            String dayFileName = restaurantName + "-" + weekStartDate + "-day.jpg";
            String weekFileName = restaurantName + "-" + weekStartDate + "-week.jpg";

            String dayMealImgPath = fileUtil.getFile("dailyMealImg", dayFileName);
            String weekMealImgPath = fileUtil.getFile("weeklyMealImg", weekFileName);


            PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                    dayMealImgPath,
                    weekMealImgPath,
                    new DailyMealResponseDTO(dayOfWeek, MealResponseDTOs)
            );

            return ResponseEntity.ok(SuccessResponse.of(
                    HttpStatus.CREATED,
                    "Daily meal plans uploaded successfully.",
                    pendingDailyMealResponseDTO
            ));
        }

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Daily meal plans uploaded successfully.",
                savedImgPath
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

    @Transactional
    public ResponseEntity<?> earlyClose(String restaurantName, EarlyCloseRequestDTO earlyCloseRequestDTO) {
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.changeIsActive(!earlyCloseRequestDTO.isEarlyClose());
        RestaurantResponseDTO response = RestaurantConverter.toResponse(restaurant);


        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}
/*
POST - http://localhost:8080/api/admin/week-meal-plans/faculty
[
    {
        "dayOfWeek": "MONDAY",
        "meals": [
            {
                "mealType": "BREAKFAST",
                "operatingStartTime": "08:00",
                "operatingEndTime": "09:30",
                "mainMenu": "불고기",
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
    },
    {
        "dayOfWeek": "WEDNESDAY",
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
            },
            {
                "mealType": "DINNER",
                "operatingStartTime": "17:00",
                "operatingEndTime": "19:00",
                "mainMenu": "메인메뉴1, 메인메뉴2",
                "subMenu": "서브메뉴1, 서브메뉴2"
            }
        ]
    }
]
 */
/*
POST - http://localhost:8080/api/admin/meal-plans/hyangseol1
{
            "dayOfWeek": "MONDAY",
            "meals": [
                {
                    "mealType": "BREAKFAST",
                    "operatingStartTime": "08:00",
                    "operatingEndTime": "09:30",
                    "mainMenu": "된장찌개3, 비빔밥3, 도시락3",
                    "subMenu": "김치2, 감자볶음2"
                },
                {
                    "mealType": "LUNCH",
                    "operatingStartTime": "11:00",
                    "operatingEndTime": "14:00",
                    "mainMenu": "불고기3, 된장찌개2",
                    "subMenu": "샐러드3, 계란찜2"
                },
                {
                    "mealType": "DINNER",
                    "operatingStartTime": "11:00",
                    "operatingEndTime": "14:00",
                    "mainMenu": "불고기3, 된장찌개2",
                    "subMenu": "샐러드3, 계란찜2"
                }
            ]
        }
 */


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
        },
        {
            "dayOfWeek": "WEDNESDAY",
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
                },
                {
                    "mealType": "DINNER",
                    "operatingStartTime": "17:00",
                    "operatingEndTime": "19:00",
                    "mainMenu": "메인메뉴1, 메인메뉴2",
                    "subMenu": "서브메뉴1, 서브메뉴2"
                }
 */