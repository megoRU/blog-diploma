package main.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TagsResponseList {

    private List<TagsResponse> tags;

    public TagsResponseList(List<TagsResponse> tags) {
        this.tags = tags;
    }
}
