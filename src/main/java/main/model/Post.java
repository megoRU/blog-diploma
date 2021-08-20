package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.model.enums.ModerationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", nullable = false)
    private int isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED') default 'NEW'", nullable = false)
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    private Integer moderatorId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Transient
    private Long postCount;

    @Transient
    private Long countLikes;

    @Transient
    private Long countDislikes;

    @Transient
    private Long sumView;

    @Transient
    LocalDateTime firstPost;

    @OneToMany(mappedBy = "post")
    private List<Tags2Post> tag;

    @OneToMany(mappedBy = "post")
    private List<PostVote> like;

    @OneToMany(mappedBy = "post")
    private List<PostComment> comment;

    public Post(Long postCount, Long countLikes, Long countDislikes, LocalDateTime firstPost) {
        this.postCount = postCount == null ? 0 : postCount;
        this.countLikes = countLikes == null ? 0 :countLikes;
        this.countDislikes = countDislikes == null ? 0 :countDislikes;
        this.firstPost = firstPost;
    }

    public Post(Long sumView, Long postCount, LocalDateTime firstPost) {
        this.sumView = sumView == null ? 0 : sumView;
        this.postCount = postCount == null ? 0 : postCount;
        this.firstPost = firstPost;
    }

    public Post(Long sumView, Long postCount) {
        this.sumView = sumView == null ? 0 : sumView;
        this.postCount = postCount == null ? 0 : postCount;
    }
}
