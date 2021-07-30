package main.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatisticsResponse {

    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;

    public StatisticsResponse(long postsCount, long likesCount, long dislikesCount, long viewsCount, long firstPublication) {
        this.postsCount = postsCount;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.viewsCount = viewsCount;
        this.firstPublication = firstPublication;
    }

    public StatisticsResponse(long number) {
        this.postsCount = number;
        this.likesCount = number;
        this.dislikesCount = number;
        this.viewsCount = number;
        this.firstPublication = number;
    }
}
