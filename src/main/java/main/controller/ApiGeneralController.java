package main.controller;

import lombok.AllArgsConstructor;
import main.api.responses.InitResponse;
import main.api.responses.SettingsResponse;
import main.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;

    @GetMapping("/api/settings")
    private ResponseEntity<SettingsResponse> settings() {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping(value = "/api/init")
    private InitResponse init() {
        return initResponse;
    }

}
