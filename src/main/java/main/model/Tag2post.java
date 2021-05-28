package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class Tag2post {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //Связь
    @Column(nullable = false)
    private int postId;

    //Связь
    @Column(nullable = false)
    private int tagId;

    public Tag2post() {}

    public Tag2post(int postId, int tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
