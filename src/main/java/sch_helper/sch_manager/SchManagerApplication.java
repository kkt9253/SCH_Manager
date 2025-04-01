package sch_helper.sch_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchManagerApplication.class, args);
    }

}
