package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;

@Entity(name = "CommentTable")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    private Date lastUpdate;
    @OneToOne
    private User owner;
    @ManyToOne
    private Solution solution;

    public Comment() {
    }

    public Comment(String text, Date lastUpdate, User owner) {
        this.text = text;
        this.lastUpdate = lastUpdate;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public User getOwner() {
        return owner;
    }

}
