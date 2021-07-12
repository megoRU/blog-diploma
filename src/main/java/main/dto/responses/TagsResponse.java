package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TagsResponse {

    private String name;
    private String weight;

    public TagsResponse(String name, String weight) {
        this.name = name;
        this.weight = weight;
    }
}
