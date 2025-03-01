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
import sch_helper.sch_manager.domain.menu.repository.MenuImageRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.util.List;

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

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 승인 상태의 menu에선 menuImage 참조가 필 X
        menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.APPROVED, null);

        List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                restaurantName,
                DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );
        // 최종 업데이트 할 때 대기 상태의 업로드된 메뉴이미지 연관관계를 유지시킨 상태로 저장해야 함
        // Master가 작성한 내용으로 덮어씌우기
        menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING, menus.get(0).getMenuImage());

        // 최종 업로드된 상태의 식단표를 DB에서 가져오기
        menus = menuUtil.getDailyMealsByMenuStatus(
                restaurantName,
                DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                MenuStatus.PENDING
        );
        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

        String weekImageName = restaurantName + "-" + weekStartDate + "-week";
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        byte[] responseWeekImage = null;
        byte[] responseDayImage = null;

        try {
            // 일주일 이미지는 필수로 존재
            responseWeekImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());

            // 특정 요일 이미지는 존재 안할 수도 있음
            // 또한 upload로직에서 관리자가 텍스트를 기입하지 않았어도 깡통 menu를 하나 만들어 저장했기 때문에 menus.get(0) 사용
            if (menus.get(0).getMenuImage() != null) {
                responseDayImage = fileUtil.encodeByteToBase64(menus.get(0).getMenuImage().getImageBinary());
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR);
        }

        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                responseDayImage,
                responseWeekImage,
                new DailyMealResponseDTO(dailyMealRequestDTO.getDayOfWeek(), MealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.of(
                HttpStatus.CREATED,
                "Daily meal plans Final approval and uploaded successfully.",
                pendingDailyMealResponseDTO
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

        String weekImageName = pendingWeeklyMealRequestDTO.getRestaurantName() + "-" + pendingWeeklyMealRequestDTO.getWeekStartDate().toString() + "-week";
        MenuImage menuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        byte[] weekMealImage = null;
        try {

            weekMealImage = fileUtil.encodeByteToBase64(menuImage.getImageBinary());
        } catch (IOException e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR);
        }

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
            throw new ApiException(ErrorCode.MENU_NOT_FOUND);
        }

        List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

        String dayOfWeek = pendingDailyMealRequestDTO.getDayOfWeek();

        byte[] dayMealImage = null;
        try {
            dayMealImage = fileUtil.encodeByteToBase64(menus.get(0).getMenuImage().getImageBinary());


            System.out.println(menus.get(0).getMenuImage().getImageName());


        } catch (IOException e) {
            throw new ApiException(ErrorCode.DATABASE_ERROR);
        }

        String weekImageName = pendingDailyMealRequestDTO.getRestaurantName() + "-" + pendingDailyMealRequestDTO.getWeekStartDate().toString() + "-week";
        MenuImage weekMenuImage = menuImageRepository.findByImageName(weekImageName)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));


        System.out.println(weekMenuImage.getImageName());


        byte[] weekMealImage = null;
        try {

            weekMealImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        PendingDailyMealResponseDTO pendingDailyMealResponseDTO = new PendingDailyMealResponseDTO(
                dayMealImage,
                weekMealImage,
                new DailyMealResponseDTO(dayOfWeek, MealResponseDTOs)
        );

        return ResponseEntity.ok(SuccessResponse.ok(pendingDailyMealResponseDTO));
    }
}
