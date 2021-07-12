package main.service;

import main.dto.responses.SettingsResponse;
import main.model.GlobalSettings;
import main.repositories.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class SettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings() {
        Iterable<GlobalSettings> globalSettings = globalSettingsRepository.findAll();
        ArrayList<GlobalSettings> list = new ArrayList<>((Collection<? extends GlobalSettings>) globalSettings);
        return new SettingsResponse(
                list.get(0).getValue().equals("YES"),
                list.get(1).getValue().equals("YES"),
                list.get(2).getValue().equals("YES"));
    }
}
