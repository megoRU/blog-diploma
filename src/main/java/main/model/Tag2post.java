package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tag2post")
public class Tag2post {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Связь
    @Column(nullable = false)
    private int postId;

    //Связь
    @Column(nullable = false)
    private int tagId;

}
