package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.enums.ModerationStatus;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Posts {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", nullable = false)
    private int isActive;

    //По умолчанию NEW!
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id", nullable = false)
    private int moderatorId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @OneToMany(mappedBy="posts")
    private List<Tag2post> tags;

    @OneToMany(mappedBy="posts")
    private List<PostVotes> like;

    @OneToMany(mappedBy="posts")
    private List<PostComments> comments;
}
