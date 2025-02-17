package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.DateUtil;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealDTO;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final FileUtil fileUtil;

    /*
    [
    {
        "dayOfWeek": "Monday",
        "meals": [
            {
                "mealType": "Breakfast",
                "operatingStartTime": "08:00",
                "operatingEndTime": "09:30",
                "mainMenu": "된장찌개, 비빔밥, 도시락",
                "subMenu": "김치, 감자볶음"
            },
            {
                "mealType": "Lunch",
                "operatingStartTime": "11:00",
                "operatingEndTime": "14:00",
                "mainMenu": "불고기, 된장찌개",
                "subMenu": "샐러드, 계란찜"
            }
        ]
    }
     */

    public ResponseEntity<?> uploadAdmin1WeekMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealDTO> dailyMealDTOS,
            MultipartFile weeklyMealImg
    ) {

        String savedImgPath = null;
        try {
            if (weeklyMealImg != null) {
                String fileName = weekStartDate + "-week.jpg";
                savedImgPath = fileUtil.saveFile(weeklyMealImg, "weeklyMealImg", fileName);
            }

            Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

            if (dailyMealDTOS != null) {
                for (DailyMealDTO dailyMealDTO : dailyMealDTOS) {
                    saveDailyMeal(dailyMealDTO, restaurant);
                }
            }

            return ResponseEntity.ok(SuccessResponse.of(
                    HttpStatus.CREATED,
                    "Weekly meal plans uploaded successfully.",
                    savedImgPath
            ));

        } catch (IOException e) {
            throw new ApiException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public void saveDailyMeal(DailyMealDTO dailyMealDTO, Restaurant restaurant) {

        DayOfWeek dayOfWeek = dailyMealDTO.getDayOfWeek();

        for (MealDTO mealDTO : dailyMealDTO.getMeals()) {

            String uniqueName = restaurant.getId() + "_" + dayOfWeek + "_" + mealDTO.getMealType();
            if (menuRepository.existsByUnique(uniqueName)) {
                menuRepository.deleteByUnique(uniqueName);
            }

            Menu menu = new Menu();
            menu.setRestaurant(restaurant);
            menu.setDayOfWeek(dayOfWeek);
            menu.setMealType(mealDTO.getMealType());
            menu.setOperatingStartTime(mealDTO.getOperatingStartTime());
            menu.setOperatingEndTime(mealDTO.getOperatingEndTime());
            menu.setMainMenu(mealDTO.getMainMenu());
            menu.setSubMenu(mealDTO.getSubMenu());
            menu.setUnique(uniqueName);
            menu.setMenuStatus(MenuStatus.PENDING);

            System.out.println("menu: " + menu);

            menuRepository.save(menu);
        }
    }
}
