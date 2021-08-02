package main.dto.responses;

import lombok.Getter;
import lombok.Setter;
import main.model.Post;
import main.model.PostVote;

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
    private String text;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
    private long viewCount;
    private ArrayList<PostCommentsResponse> comments;
    private ArrayList<String> tags;

    public PostResponseForList(Post post, List<PostCommentsResponse> comments, List<String> tags) {
        this.id = post.getId();
        this.timestamp = post.getTime().toEpochSecond(ZoneOffset.UTC);
        this.user = new UserPostResponse(post.getUser());
        this.title = post.getTitle();
        this.text = post.getText();
        this.announce = setAnnounce(post);
        this.likeCount = getLikeCount(post);
        this.dislikeCount = getDislikeCount(post);
        this.commentCount = setCommentCount(post);
        this.viewCount = post.getViewCount();
        this.comments = new ArrayList<>(comments);
        this.tags = new ArrayList<>(tags);
    }

    public PostResponseForList(Post post) {
        this.id = post.getId();
        this.timestamp = post.getTime().toEpochSecond(ZoneOffset.UTC);
        this.user = new UserPostResponse(post.getUser());
        this.title = post.getTitle();
        this.text = post.getText();
        this.announce = setAnnounce(post);
        this.likeCount = getLikeCount(post);
        this.dislikeCount = getDislikeCount(post);
        this.commentCount = setCommentCount(post);
        this.viewCount = post.getViewCount();
    }

    /**
     * Исправляет баг с очень большими баблами на фронте. Да это мини костыль, но что поделать!
     */
    private String setAnnounce(Post post) throws ArrayIndexOutOfBoundsException {
        if (post.getText().lastIndexOf("</div>") == -1 && post.getText().lastIndexOf(System.lineSeparator()) == -1) {
            return post.getText()
                    .replaceAll("<.*?>", "")
                    .replaceAll("&nbsp;", " ")
                    .substring(0, 90) + "...";
        }

        return post.getText().length() >= 150 ? post.getText()
                .replaceAll("</div>", " ")
                .replaceAll("<.*?>", "")
                .replaceAll("&nbsp;", " ")
                .substring(0, 150) + "..." : post.getText();
    }

    private long getLikeCount(Post post) {
        likeCount = 0;

        if (post.getLike() != null) {
            List<PostVote> like = new ArrayList<>(post.getLike());
            for (PostVote l : like) {
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
            List<PostVote> like = new ArrayList<>(post.getLike());
            for (PostVote l : like) {
                if (l.getValue() == -1) {
                    dislikeCount++;
                }
            }
        }
        return dislikeCount;
    }

    private long setCommentCount(Post post) {

        if (post.getComment() != null) {
            commentCount = post.getComment().size();
        } else {
            commentCount = 0;
        }

        return commentCount;
    }
}
