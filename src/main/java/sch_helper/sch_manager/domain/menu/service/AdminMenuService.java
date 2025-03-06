package sch_helper.sch_manager.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.SuccessResponse;
import sch_helper.sch_manager.common.util.FileUtil;
import sch_helper.sch_manager.common.util.MenuUtil;
import sch_helper.sch_manager.domain.menu.dto.*;
import sch_helper.sch_manager.domain.menu.dto.base.*;
import sch_helper.sch_manager.domain.menu.dto.converter.*;
import sch_helper.sch_manager.domain.menu.entity.*;
import sch_helper.sch_manager.domain.menu.enums.DayOfWeek;
import sch_helper.sch_manager.domain.menu.enums.MealType;
import sch_helper.sch_manager.domain.menu.enums.MenuStatus;
import sch_helper.sch_manager.domain.menu.enums.RestaurantName;
import sch_helper.sch_manager.domain.menu.repository.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    // repository
    private final RestaurantRepository restaurantRepository;
    private final WeeklyMenuRepository weeklyMenuRepository;
    private final DailyMenuRepository dailyMenuRepository;
//    private final MealRepository mealRepository;
    private final MenuImageRepository menuImageRepository;

    // converter
    private final WeeklyMenuConverter weeklyMenuConverter;
    private final DailyMenuConverter dailyMenuConverter;
    private final MealConverter mealConverter;

    // utils
    private final FileUtil fileUtil;
    private final MenuUtil menuUtil;

    /**
     * Refactoring 필요
     * 수정, 저장 모두 가능하도록 변경해야함
     * */
    @Transactional
    public ResponseEntity<?> uploadWeeklyMealPlans(
            String restaurantName,
            String weekStartDate,
            List<DailyMealRequestDTO> dailyMealRequestDTOs,
            MultipartFile weeklyMealImg
    ) {
        byte[] byteImage = fileUtil.imageToByte(weeklyMealImg);
        LocalDate parsedWeekStartDate = LocalDate.parse(weekStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        Optional<WeeklyMenu> existingWeeklyMenuOpt = weeklyMenuRepository.findByRestaurantAndWeekStartDate(restaurant, parsedWeekStartDate);

        WeeklyMenu saved;
        if (existingWeeklyMenuOpt.isPresent()) {
            WeeklyMenu existingWeeklyMenu = existingWeeklyMenuOpt.get();
            existingWeeklyMenu.setWeeklyImage(byteImage);

            dailyMealRequestDTOs.forEach(dailyMealRequestDTO -> {
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek());

                // 1. 기존 DailyMenu 찾기
                Optional<DailyMenu> existingDailyMenuOpt = existingWeeklyMenu.getDailyMenuList().stream()
                        .filter(dm -> dm.getDayOfWeek() == dayOfWeek)
                        .findFirst();

                if (existingDailyMenuOpt.isPresent()) {
                    DailyMenu existingDailyMenu = existingDailyMenuOpt.get();
                    List<Meal> existingMeals = existingDailyMenu.getMealList();

                    // 2. 요청된 MealType 목록 추출
                    Set<MealType> requestMealTypes = dailyMealRequestDTO.getMeals().stream()
                            .map(mealDto -> MealType.valueOf(mealDto.getMealType()))
                            .collect(Collectors.toSet());

                    // 3. 기존 Meal 업데이트 또는 추가
                    dailyMealRequestDTO.getMeals().forEach(mealDto -> {
                        Optional<Meal> existingMealOpt = existingMeals.stream()
                                .filter(m -> m.getMealType() == MealType.valueOf(mealDto.getMealType()))
                                .findFirst();

                        if (existingMealOpt.isPresent()) {
                            // 3-1. 기존 Meal 업데이트
                            Meal existingMeal = existingMealOpt.get();
                            existingMeal.updateFromDto(mealDto); // 엔터티에 업데이트 메서드 추가
                        } else {
                            // 3-2. 새로운 Meal 추가
                            Meal newMeal = mealConverter.toEntity(mealDto, existingDailyMenu);
                            existingMeals.add(newMeal);
                        }
                    });

                } else {
                    // 새로운 DailyMenu 추가
                    DailyMenu newDailyMenu = dailyMenuConverter.toEntity(dailyMealRequestDTO, existingWeeklyMenu);
                    existingWeeklyMenu.getDailyMenuList().add(newDailyMenu);
                    dailyMealRequestDTO.getMeals().forEach(mealDto -> {
                        Meal meal = mealConverter.toEntity(mealDto, newDailyMenu);
                        newDailyMenu.getMealList().add(meal);
                    });
                }
            });

            saved = weeklyMenuRepository.save(existingWeeklyMenu);
        } else {
            // 주간 식단을 처음 등록하는 경우 모든 Entity 새로 생성 후 저장
            WeeklyMenu newWeeklyMenu = WeeklyMenu.builder()
                    .weekStartDate(parsedWeekStartDate)
                    .weekEndDate(parsedWeekStartDate.plusDays(5))
                    .weeklyImage(byteImage)
                    .restaurant(restaurant)
                    .dailyMenuList(new ArrayList<>())
                    .build();

            dailyMealRequestDTOs.forEach(dto -> {
                DailyMenu dailyMenu = dailyMenuConverter.toEntity(dto, newWeeklyMenu);
                newWeeklyMenu.getDailyMenuList().add(dailyMenu);
                dto.getMeals().forEach(mealDto -> {
                    Meal meal = mealConverter.toEntity(mealDto, dailyMenu);
                    dailyMenu.getMealList().add(meal);
                });
            });

            saved = weeklyMenuRepository.save(newWeeklyMenu);
        }

        return ResponseEntity.ok(SuccessResponse.ok(weeklyMenuConverter.toResponse(saved)));
    }

    @Transactional
    public ResponseEntity<?> uploadDailyMealPlans(
            String restaurantName,
            String weekStartDate,
            DailyMealRequestDTO dailyMealRequestDTO,
            MultipartFile dailyMealImg
    ) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        WeeklyMenu weeklyMenuEntity = weeklyMenuRepository.findByRestaurantAndWeekStartDate(restaurant, LocalDate.parse(weekStartDate, dateTimeFormatter))
                .orElseThrow(() -> new ApiException(ErrorCode.WEEKLY_MENU_NOT_FOUND));

        DailyMenu dailyMenu = dailyMenuRepository.findByWeeklyMenuAndDayOfWeek(weeklyMenuEntity, DayOfWeek.valueOf(dailyMealRequestDTO.getDayOfWeek()))
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
        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.changeIsActive(!earlyCloseRequestDTO.isEarlyClose());
        RestaurantResponseDTO response = RestaurantConverter.toResponse(restaurant);


        return ResponseEntity.ok(SuccessResponse.ok(response));
    }

    @Transactional
    public ResponseEntity<?> updateTotalOperatingTime(String restaurantName, TotalOperatingTimeRequestDTO request) {
        Restaurant restaurant = restaurantRepository.findByName(RestaurantName.valueOf(restaurantName))
                .orElseThrow(() -> new ApiException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurant.changeOperatingStartTime(request.getNewOperatingStartTime());
        restaurant.changeOperatingEndTime(request.getNewOperatingEndTime());

        RestaurantResponseDTO response = RestaurantConverter.toResponse(restaurant);

        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}