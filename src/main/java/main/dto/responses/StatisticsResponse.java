package main.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {

    private Long postsCount;
    private Long likesCount;
    private Long dislikesCount;
    private Long viewsCount;
    private Long firstPublication;

    public StatisticsResponse(Long postCount, Long countLikes, Long countDislikes, Long sumView, LocalDateTime firstPostDate) {
        this.postsCount = postCount;
        this.likesCount = countLikes;
        this.dislikesCount = countDislikes;
        this.viewsCount = sumView;
        this.firstPublication = Optional.ofNullable(firstPostDate).map(date -> date.toEpochSecond(ZoneOffset.UTC)).orElse(null);
    }

}
