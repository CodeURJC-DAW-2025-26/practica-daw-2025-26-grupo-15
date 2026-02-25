package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Date;

@Entity(name = "CommentTable")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    private Date lastUpdate;
    @ManyToOne
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

    public Solution getSolution() {
        return solution;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }


}
