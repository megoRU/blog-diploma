package main.service;

import lombok.RequiredArgsConstructor;
import main.dto.request.SettingsRequest;
import main.dto.responses.ResultResponse;
import main.dto.responses.SettingsResponse;
import main.model.GlobalSettings;
import main.repositories.GlobalSettingsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {
        Iterable<GlobalSettings> globalSettings = globalSettingsRepository.findAll();
        ArrayList<GlobalSettings> list = new ArrayList<>((Collection<? extends GlobalSettings>) globalSettings);
        return new SettingsResponse(
                list.get(0).getValue().equals("YES"),
                list.get(1).getValue().equals("YES"),
                list.get(2).getValue().equals("YES"));
    }

    public ResponseEntity<?> editSettings(SettingsRequest settingsRequest) {
        globalSettingsRepository.insertSettings("MULTIUSER_MODE", settingsRequest.isMultiuseMode() ? "YES" : "NO");
        globalSettingsRepository.insertSettings("POST_PREMODERATION", settingsRequest.isPostPremoderation() ? "YES" : "NO");
        globalSettingsRepository.insertSettings("STATISTICS_IS_PUBLIC", settingsRequest.isStatisticIsPublic() ? "YES" : "NO");

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }
}
