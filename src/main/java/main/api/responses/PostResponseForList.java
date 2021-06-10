package main.api.responses;

import lombok.Getter;
import lombok.Setter;
import main.model.PostVotes;
import main.model.Post;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponseForList {

    private long id;
    private long timestamp;
    private UserPostResponse user;
    private String title;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private long viewCount;

    public PostResponseForList(Post post) {
        this.id = post.getId();
        this.timestamp = post.getTime().toEpochSecond(ZoneOffset.ofHours(3));
        this.user = new UserPostResponse(post.getUser());
        this.title = post.getTitle();
        this.announce = setAnnounce(post);
        this.likeCount = getLikeCount(post);
        this.dislikeCount = getDislikeCount(post);
        this.commentCount = setCommentCount(post);
        this.viewCount = post.getViewCount();
    }

    private String setAnnounce(Post post) {
        String announce = post.getText()
                .replaceAll("</div>", " ")
                .replaceAll("<.*?>", "")
                .replaceAll("&nbsp;", " ");

        if (announce.length() > 400) {
            return announce.substring(0, 400) + "...";
        }
        return announce;
    }

    private long getLikeCount(Post post) {
        likeCount = 0;

        if (post.getLike() != null) {
            List<PostVotes> like = new ArrayList<>(post.getLike());
            for (PostVotes l : like) {
                if (l.getValue() == 1) {
                    likeCount++;
                }

            }
        }
        return likeCount;
    }

    private long getDislikeCount(Post post) {
        dislikeCount = 0;

        if (post.getLike() != null) {
            List<PostVotes> like = new ArrayList<>(post.getLike());
            for (PostVotes l : like) {
                if (l.getValue() == -1) {
                    dislikeCount++;
                }
            }
        }
        return dislikeCount;
    }

    private long setCommentCount(Post post) {

        if (post.getComments() != null) {
            commentCount = post.getComments().size();
        } else {
            commentCount = 0;
        }

        return commentCount;
    }
}
