package sch_helper.sch_manager.common.util;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
public class DateUtil {

    public DayOfWeek getDayOfWeek(String date) {

        LocalDate localDate = LocalDate.parse(date);
        return localDate.getDayOfWeek();
    }

    public boolean isSameDayOfWeek(String date, DayOfWeek dayOfWeek) {

        return getDayOfWeek(date).equals(dayOfWeek);
    }

}
