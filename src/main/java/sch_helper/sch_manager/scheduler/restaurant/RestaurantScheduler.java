package sch_helper.sch_manager.scheduler.restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sch_helper.sch_manager.domain.menu.entity.Restaurant;
import sch_helper.sch_manager.domain.menu.repository.RestaurantRepository;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantScheduler {

    private static final Map<LocalTime, Boolean> SCHEDULE_MAP = Map.of(
            LocalTime.of(8, 0), true,    // 08:00 활성화
            LocalTime.of(11, 0), true,   // 11:00 활성화
            LocalTime.of(17, 0), true,   // 17:00 활성화
            LocalTime.of(9, 30), false,  // 09:30 비활성화
            LocalTime.of(14, 0), false,  // 14:00 비활성화
            LocalTime.of(19, 0), false  // 19:00 비활성화
    );

    private final RestaurantRepository restaurantRepository;

    @Scheduled(cron = "0 0 8,11,17 * * *", zone = "Asia/Seoul") // 활성화 시간대 (08:00, 11:00, 17:00)
    @Scheduled(cron = "0 30 9 * * *", zone = "Asia/Seoul")      // 비활성화 시간대 (09:30)
    @Scheduled(cron = "0 0 14,19 * * *", zone = "Asia/Seoul")  // 비활성화 시간대 (14:00, 19:00)
    public void updateRestaurantStatus() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES); // 초 제거
        log.info("식당 상태 강제 업데이트 실행 - {}", now);

        if (!SCHEDULE_MAP.containsKey(now)) {
            log.info("해당 시간대 작업 없음");
            return;
        }

        boolean targetStatus = SCHEDULE_MAP.get(now);
        List<Restaurant> targets = restaurantRepository.findByIsActive(!targetStatus);

        targets.forEach(restaurant -> {
            restaurant.changeIsActive(targetStatus);
            log.debug("{} 상태 변경: {} → {}",
                    restaurant.getName(), !targetStatus, targetStatus);
        });

        restaurantRepository.saveAll(targets);
        log.info("{}개 식당 {} 처리", targets.size(),
                targetStatus ? "활성화" : "비활성화");
    }

}


