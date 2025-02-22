package sch_helper.sch_manager.domain.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuQueryDslRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final FileUtil fileUtil;
    private final ObjectMapper objectMapper;
    private final MenuUtil menuUtil;
    private final MenuQueryDslRepository menuQueryDslRepository;


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

                    String jsonDailyMealDTO = objectMapper.writeValueAsString(dailyMealRequestDTO);
                    System.out.println("jsonDailyMealDTO => \n" + jsonDailyMealDTO);

                    menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
                }
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
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

            Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (dailyMealRequestDTO != null) {

                String jsonDailyMealDTO = objectMapper.writeValueAsString(dailyMealRequestDTO);
                System.out.println("jsonDailyMealDTO => \n" + jsonDailyMealDTO);

                menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "daily meal plans uploaded successfully.",
                savedImgPath
        ));
    }


    public ResponseEntity<?> getPendingDailyMealPlans(PendingDailyMealRequestDTO pendingDailyMealRequestDTO) {

        List<Menu> menus = menuQueryDslRepository.getDailyMealByMenuStatus(
                pendingDailyMealRequestDTO.getRestaurantName(),
                DayOfWeek.valueOf(pendingDailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );

        List<MealResponseDTO> MealResponseDTOs = MenuConverter.toMealResponseDTOs(menus);

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

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("jsonMealResponseDTOs => \n" + objectMapper.writeValueAsString(MealResponseDTOs));
            System.out.println("pendingDailyMealResponseDTO => \n" + objectMapper.writeValueAsString(pendingDailyMealResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
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