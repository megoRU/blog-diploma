package main.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePost {

    private long timestamp;
    private Integer active;
    private String title;
    private ArrayList<String> tags;
    private String text;
}

