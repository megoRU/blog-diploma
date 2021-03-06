package main.controller;

import lombok.RequiredArgsConstructor;
import main.dto.request.ProfileRequest;
import main.dto.request.SettingsRequest;
import main.dto.responses.CalendarResponseList;
import main.dto.responses.InitResponse;
import main.dto.responses.SettingsResponse;
import main.dto.responses.StatisticsResponse;
import main.repositories.GlobalSettingsRepository;
import main.security.UserService;
import main.service.CalendarService;
import main.service.ImageService;
import main.service.SettingsService;
import main.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final CalendarService calendarService;
    private final StatisticsService statisticsService;
    private final ImageService imageService;
    private final UserService userService;
    private final GlobalSettingsRepository globalSettingsRepository;

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
    private ResponseEntity<StatisticsResponse> getMyStatistics(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return statisticsService.getMyStatistics();
    }

    @GetMapping(value = "/api/statistics/all")
    private ResponseEntity<StatisticsResponse> getAllStatistics(Principal principal) {
        if (globalSettingsRepository.getSettingsById("STATISTICS_IS_PUBLIC").getValue().equals("NO") && principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (globalSettingsRepository.getSettingsById("STATISTICS_IS_PUBLIC").getValue().equals("NO")
                && userService.getCurrentUser().getIsModerator() == 0) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return statisticsService.getAllStatistics();
    }

    @PostMapping(value = "/api/profile/my", consumes = {"multipart/form-data"})
    public ResponseEntity<?> editProfile(
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "email", required = false) String email,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "removePhoto", required = false) String removePhoto,
            Principal principal) throws Exception {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return imageService.editProfileImage(photo, name, email, password, removePhoto, principal);
    }

    @PostMapping(value = "/api/profile/my", consumes = {"application/json"})
    public ResponseEntity<?> editProfileJSON(
            @RequestBody(required = false) ProfileRequest profileRequest,
            Principal principal) throws Exception {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return imageService.editProfile(profileRequest, principal);
    }

    @PostMapping(value = "/api/image", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadImage(
            @RequestPart(value = "image", required = false) MultipartFile photo,
            Principal principal) throws Exception {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return imageService.uploadImage(photo, principal);
    }

}