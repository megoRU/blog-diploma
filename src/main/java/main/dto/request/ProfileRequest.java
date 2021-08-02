package main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileRequest {

    private String photo;
    private String name;
    private String email;
    private String password;
    private int removePhoto;
}