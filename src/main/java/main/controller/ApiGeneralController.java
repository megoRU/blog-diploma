package main.controller;

import lombok.RequiredArgsConstructor;
import main.dto.responses.CalendarResponseList;
import main.dto.responses.InitResponse;
import main.dto.responses.SettingsResponse;
import main.service.CalendarService;
import main.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final CalendarService calendarService;

    @GetMapping(value = "/api/settings")
    private ResponseEntity<SettingsResponse> settings() {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping(value = "/api/init")
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping(value = "/api/calendar")
    private CalendarResponseList getCalendar(
            @RequestParam(required = false, defaultValue = "") String year) {
        return calendarService.getPublications(year);
    }

}
