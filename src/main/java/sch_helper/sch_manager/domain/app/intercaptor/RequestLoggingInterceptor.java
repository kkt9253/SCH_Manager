package sch_helper.sch_manager.domain.app.intercaptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sch_helper.sch_manager.domain.app.entity.RequestLog;
import sch_helper.sch_manager.domain.app.repository.RequestLogRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final RequestLogRepository requestLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        LocalDateTime requestTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(startTime),
                ZoneId.systemDefault()
        );

        RequestLog log = RequestLog.builder()
                .method(request.getMethod())
                .uri(request.getRequestURI())
                .statusCode(response.getStatus())
                .responseTimeMs(responseTime)
                .requestTime(requestTime)
                .build();

        System.out.println(log.toString());
        requestLogRepository.save(log);
    }
}
