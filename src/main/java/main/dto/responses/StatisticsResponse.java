package main.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {

    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;
}
