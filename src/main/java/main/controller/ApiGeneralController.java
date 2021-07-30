package main.controller;

import lombok.RequiredArgsConstructor;
import main.dto.request.SettingsRequest;
import main.dto.responses.CalendarResponseList;
import main.dto.responses.InitResponse;
import main.dto.responses.SettingsResponse;
import main.service.CalendarService;
import main.service.SettingsService;
import main.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final CalendarService calendarService;
    private final StatisticsService statisticsService;

    @GetMapping(value = "/api/settings")
    private ResponseEntity<SettingsResponse> settings() {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @PutMapping(value = "/api/settings")
    private ResponseEntity<?> editSettings(
            Principal principal,
            @RequestBody SettingsRequest settingsRequest) {

        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return settingsService.editSettings(settingsRequest);
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

    @GetMapping(value = "/api/statistics/my")
    private ResponseEntity<?> getMyStatistics(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return statisticsService.getMyStatistics();
    }

    @GetMapping(value = "/api/statistics/all")
    private ResponseEntity<?> getAllStatistics(Principal principal) {
        return statisticsService.getAllStatistics(principal);
    }

}
