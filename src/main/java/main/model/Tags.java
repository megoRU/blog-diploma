package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tags {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String name;

    public Tags() {}

    public Tags(String name) {
        this.name = name;
    }
}
