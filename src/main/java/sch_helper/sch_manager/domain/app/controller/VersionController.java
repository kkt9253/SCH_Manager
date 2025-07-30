package sch_helper.sch_manager.domain.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sch_helper.sch_manager.common.response.SuccessResponse;

@RestController
@RequestMapping("/api")
public class VersionController {

    private final String appVersion;

    public VersionController(@Value("${spring.app.version}") String appVersion) {
        this.appVersion = appVersion;
    }

    @GetMapping("/version")
    public ResponseEntity<SuccessResponse<String>> getVersion() {

        return ResponseEntity.ok(SuccessResponse.ok(appVersion));
    }
}
