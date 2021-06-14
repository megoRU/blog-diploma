package main.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequest {

    private boolean multiuseMode;
    private boolean postPremoderation;
    private boolean statisticIsPublic;

}
