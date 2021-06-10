package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
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
