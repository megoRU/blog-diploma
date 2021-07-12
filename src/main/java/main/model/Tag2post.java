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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id")
    private Posts posts;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id", nullable = false, referencedColumnName = "id")
    private Tags tags;
}
