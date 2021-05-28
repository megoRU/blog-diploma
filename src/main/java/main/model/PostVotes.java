package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
public class PostVotes {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //Тут связь должна быть
    @Column(name = "user_id", nullable = false)
    private int userId;

    //Тут связь должна быть
    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private int value;

    public PostVotes() {}

    public PostVotes(int userId, int postId, LocalDateTime time, int value) {
        this.userId = userId;
        this.postId = postId;
        this.time = time;
        this.value = value;
    }
}
