package main.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileRequest {

    private String photo;
    private String name;
    private String email;
    private String password;
    private int removePhoto;

}
