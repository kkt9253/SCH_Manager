package sch_helper.sch_manager.domain.menu.service;

import jakarta.transaction.Transactional;
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
import sch_helper.sch_manager.domain.menu.dto.*;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealRequestDTO;
import sch_helper.sch_manager.domain.menu.dto.base.DailyMealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.MealResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.base.RestaurantResponseDTO;
import sch_helper.sch_manager.domain.menu.dto.converter.MenuConverter;
import sch_helper.sch_manager.domain.menu.dto.converter.RestaurantConverter;
import sch_helper.sch_manager.domain.menu.entity.Menu;
import sch_helper.sch_manager.domain.menu.entity.MenuImage;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuImageRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final RestaurantRepository restaurantRepository;
    private final FileUtil fileUtil;
    private final MenuUtil menuUtil;
    private final MenuImageRepository menuImageRepository;

    @Transactional
    public ResponseEntity<?> uploadWeeklyMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealRequestDTO> dailyMealRequestDTOs,
            MultipartFile weeklyMealImg
    ) {

        String imageName = restaurantName + "-" + weekStartDate + "-week";
        byte[] byteImage = fileUtil.imageToByte(weeklyMealImg);
        byte[] encodeImage = fileUtil.encodeByteToBase64(byteImage);

        MenuImage menuImage = menuImageRepository.findByImageName(imageName)
                        .orElse(new MenuImage());
        menuImage.setImageName(imageName);
        menuImage.setImageBinary(byteImage);

        menuImageRepository.save(menuImage);

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (dailyMealRequestDTOs != null) {
            for (DailyMealRequestDTO dailyMealRequestDTO : dailyMealRequestDTOs) {

                menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);
            }

            List<Menu> menus = menuUtil.getWeeklyMealsByMenuStatus(
                    restaurantName,
                    MenuStatus.PENDING
            );
            List<DailyMealResponseDTO> DailyMealResponseDTOs = MenuConverter.getDailyMealResponseDTOsByMenus(menus);

            PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                    encodeImage,
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
                encodeImage
        ));
    }

    @Transactional
    public ResponseEntity<?> uploadDailyMealPlans(
            String restaurantName,
            String dayOfWeek,
            String weekStartDate,
            DailyMealRequestDTO dailyMealRequestDTO,
            MultipartFile dailyMealImg
    ) {

        String imageName = restaurantName + "-" + weekStartDate + "-" + dayOfWeek;
        byte[] byteImage = fileUtil.imageToByte(dailyMealImg);
        byte[] encodeImage = fileUtil.encodeByteToBase64(byteImage);

        MenuImage menuImage = menuImageRepository.findByImageName(imageName)
                .orElse(new MenuImage());
        menuImage.setImageName(imageName);
        menuImage.setImageBinary(byteImage);

        menuImageRepository.save(menuImage);

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (dailyMealRequestDTO != null) {

            // 특정 요일의 식단표 이미지는 연관관계 매핑 해줘야 함
            menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING);

            List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                    restaurantName,
                    DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                    MenuStatus.PENDING
            );
            List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

            // 일주일치 식단표 이미지 참조
            MenuImage weekMenuImage = menuImageRepository.findByImageName(restaurantName + "-" + weekStartDate + "-week")
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

            byte[] encodeWeekImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

            PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                    encodeImage,
                    encodeWeekImage,
                    new DailyMealResponseDTO(dailyMealRequestDTO.getDayOfWeek(), MealResponseDTOs)
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
                encodeImage
        ));
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
        MenuImage menuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        byte[] weekMealImage = fileUtil.encodeByteToBase64(menuImage.getImageBinary());

        PendingWeeklyMealResponseDTO pendingWeeklyMealResponseDTO = new PendingWeeklyMealResponseDTO(
                weekMealImage,
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

    @Transactional
    public ResponseEntity<?> earlyClose(String restaurantName, EarlyCloseRequestDTO earlyCloseRequestDTO) {
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.changeIsActive(!earlyCloseRequestDTO.isEarlyClose());
        RestaurantResponseDTO response = RestaurantConverter.toResponse(restaurant);


        return ResponseEntity.ok(SuccessResponse.ok(response));
    }

    @Transactional
    public ResponseEntity<?> updateTotalOperatingTime(String restaurantName, TotalOperatingTimeRequestDTO request) {
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.changeOperatingStartTime(request.getNewOperatingStartTime());
        restaurant.changeOperatingEndTime(request.getNewOperatingEndTime());

        RestaurantResponseDTO response = RestaurantConverter.toResponse(restaurant);

        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}