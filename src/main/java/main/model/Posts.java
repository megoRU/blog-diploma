package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Posts {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "is_active", nullable = false)
    private int isActive;

    //По умолчанию NEW!
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus;

    //Тут связь должна быть
    @Column(name = "moderator_id", nullable = false)
    private int moderatorId;

    //Тут связь должна быть
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    public Posts() {}

    public Posts(int isActive, ModerationStatus moderationStatus, int moderatorId, int userId, LocalDateTime time, String title, String text, int viewCount) {
        this.isActive = isActive;
        this.moderationStatus = moderationStatus;
        this.moderatorId = moderatorId;
        this.userId = userId;
        this.time = time;
        this.title = title;
        this.text = text;
        this.viewCount = viewCount;
    }
}
