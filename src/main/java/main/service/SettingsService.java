package main.service;

import main.dto.responses.SettingsResponse;
import main.repositories.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(globalSettingsRepository.findAllGlobalSettings("MULTIUSER_MODE").getValue().equals("YES"));
        settingsResponse.setPostPremoderation(globalSettingsRepository.findAllGlobalSettings("POST_PREMODERATION").getValue().equals("YES"));
        settingsResponse.setStatisticIsPublic(globalSettingsRepository.findAllGlobalSettings("STATISTICS_IS_PUBLIC").getValue().equals("YES"));
        return settingsResponse;
    }
}
