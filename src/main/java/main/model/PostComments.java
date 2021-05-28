package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
public class PostComments {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "parent_id")
    private int parentId;

    //Тут связь должна быть
    @Column(name = "post_id", nullable = false)
    private int postId;

    //Тут связь должна быть
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String text;

    public PostComments() {}

    public PostComments(int parentId, int postId, int userId, LocalDateTime time, String text) {
        this.parentId = parentId;
        this.postId = postId;
        this.userId = userId;
        this.time = time;
        this.text = text;
    }
}
