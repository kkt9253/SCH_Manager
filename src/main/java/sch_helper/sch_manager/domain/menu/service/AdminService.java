package sch_helper.sch_manager.domain.menu.service;

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
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealDTO;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;
import sch_helper.sch_manager.domain.user.enums.Role;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final FileUtil fileUtil;
    private final ObjectMapper objectMapper;
    private final MenuUtil menuUtil;


    public ResponseEntity<?> uploadAdmin1WeekMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealDTO> dailyMealDTOS,
            MultipartFile weeklyMealImg
    ) {

        String savedImgPath = null;
        try {

            String fileName = weekStartDate + "-week.jpg";
            savedImgPath = fileUtil.saveFile(weeklyMealImg, "weeklyMealImg", fileName);

            Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (dailyMealDTOS != null) {
                for (DailyMealDTO dailyMealDTO : dailyMealDTOS) {

                    String jsonDailyMealDTO = objectMapper.writeValueAsString(dailyMealDTO);
                    System.out.println("jsonDailyMealDTO => \n" + jsonDailyMealDTO);

                    menuUtil.saveDailyMeal(restaurant, dailyMealDTO, MenuStatus.PENDING);
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




}


/*
{
    "dailyMeals": [
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
}
*/