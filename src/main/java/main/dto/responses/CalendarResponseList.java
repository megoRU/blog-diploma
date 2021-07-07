package main.dto.responses;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CalendarResponseList {

    private Set<Integer> years;
    private Map<String, Integer> posts;

    public CalendarResponseList(Set<Integer> years, Map<String, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }
}
