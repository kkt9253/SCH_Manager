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
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.repository.MenuImageRepository;
import sch_helper.sch_manager.domain.menu.repository.MenuRepository;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final RestaurantRepository restaurantRepository;
    private final FileUtil fileUtil;
    private final MenuUtil menuUtil;
    private final MenuImageRepository menuImageRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public ResponseEntity<?> uploadWeeklyMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealRequestDTO> dailyMealRequestDTOs,
            MultipartFile weeklyMealImg
    ) {

        String imageName = null;
        byte[] byteImage = null;
        byte[] encodeImage = null;
        try {
            imageName = restaurantName + "-" + weekStartDate + "-week";
            byteImage = fileUtil.imageToByte(weeklyMealImg);
            encodeImage = fileUtil.encodeByteToBase64(byteImage);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        MenuImage menuImage = menuImageRepository.findByImageName(imageName)
                        .orElse(new MenuImage());
        menuImage.setImageName(imageName);
        menuImage.setImageBinary(byteImage);

        menuImageRepository.save(menuImage);

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (dailyMealRequestDTOs != null) {
            for (DailyMealRequestDTO dailyMealRequestDTO : dailyMealRequestDTOs) {

                // 일주일치 식단표 이미지는 menuImage-WeekInfo 필드를 통해 참조할 것이기 때문에 null
                menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING, null);
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

        String imageName = null;
        byte[] byteImage = null;
        byte[] encodeImage = null;
        try {

            imageName = restaurantName + "-" + weekStartDate + "-day";
            byteImage = fileUtil.imageToByte(dailyMealImg);
            encodeImage = fileUtil.encodeByteToBase64(byteImage);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.UPLOAD_FAILED);
        }

        // 만약 재업로드라면 이전의 이미지가 남아있고 덮어씌우지 않도록 동작하지만, 어차피 menu -> menuImage_id 참조를 통해 이미지 가져오기 때문에 상관 없음
        MenuImage menuImage = new MenuImage();
        menuImage.setImageName(imageName);
        menuImage.setImageBinary(byteImage);
        menuImageRepository.save(menuImage);

        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));


        // 식단표 업로드 api들의 요청 파라미터에서 이미지는 필수이지만, 식단표 텍스트는 필수가 아님.
        // 업로드 시 식단표 텍스트 존재 O
        if (dailyMealRequestDTO != null) {

            // 특정 요일의 식단표 이미지는 연관관계 매핑 해줘야 함
            menuUtil.saveDailyMeal(restaurant, dailyMealRequestDTO, MenuStatus.PENDING, menuImage);

            List<Menu> menus = menuUtil.getDailyMealsByMenuStatus(
                    restaurantName,
                    DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()),
                    MenuStatus.PENDING
            );
            List<MealResponseDTO> MealResponseDTOs = MenuConverter.getMealResponseDTOsByMenus(menus);

            // 일주일치 식단표 이미지 참조
            MenuImage weekMenuImage = menuImageRepository.findByImageName(restaurantName + "-" + weekStartDate + "-week")
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

            byte[] encodeWeekImage = null;
            try {

                encodeWeekImage = fileUtil.encodeByteToBase64(weekMenuImage.getImageBinary());
            } catch (IOException e) {
                throw new ApiException(ErrorCode.DATABASE_ERROR);
            }

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

        // 업로드 시 식단표 텍스트 존재 X
        // 하지만 특정요일 이미지는 menu->menuImage 참조를 하기 때문에 이미지만 업로드하더라도 참조가 가능하도록 DB에 빈 컬럼이더라도 연관관계 설정이 필요함
        List<Menu> menus = new ArrayList<>();
        for (MealType mealType : MealType.values()) {

            String uniqueName = restaurant.getId() + "_" + dayOfWeek + "_" + mealType.name() + "_" + MenuStatus.PENDING.name();
            Menu menu = menuRepository.findByUnique(uniqueName)
                    .orElse(new Menu());

            menus.add(menu);
        }

        // 이미 식단표가 존재하고, 재업로드 된 상황이면 덮어쓰기
        if (!menus.isEmpty()) {

            for (Menu menu : menus) {

                menu.setRestaurant(menu.getRestaurant());
                menu.setDayOfWeek(menu.getDayOfWeek());
                menu.setMealType(menu.getMealType());
                menu.setOperatingStartTime(menu.getOperatingStartTime());
                menu.setOperatingEndTime(menu.getOperatingEndTime());
                menu.setMainMenu(menu.getMainMenu());
                menu.setSubMenu(menu.getSubMenu());
                menu.setUnique(menu.getUnique());
                menu.setMenuStatus(menu.getMenuStatus());
                menu.setMenuImage(menuImage);

                menuRepository.save(menu);
            }
        }
        // 존재하지 않는다면 menu컬럼이 없기 때문에 연관관계을 위해 깡통 menu를 만들어 특정요일 이미지와 연결시켜야 함
        else {

            String uniqueName = restaurant.getId() + "_" + dayOfWeek + "_" + MealType.LUNCH.name() + "_" + MenuStatus.PENDING.name();
            Menu menu = new Menu();

            menu.setRestaurant(restaurant);
            menu.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek));
            // 향1, 교직원 식당에서 공통으로 점심은 필수이기 때문에 Lunch로 설정
            menu.setMealType(MealType.LUNCH);
            menu.setOperatingStartTime("temp");
            menu.setOperatingEndTime("temp");
            menu.setMainMenu("temp");
            menu.setSubMenu("temp");
            menu.setUnique(uniqueName);
            menu.setMenuStatus(MenuStatus.PENDING);
            menu.setMenuImage(menuImage);

            menuRepository.save(menu);
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
            // front: 식단표가 업로드되지 않았습니다. 화면으로 설정
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