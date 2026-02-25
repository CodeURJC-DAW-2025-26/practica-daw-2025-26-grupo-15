package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.List;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.sql.Date;

@Entity(name = "SolutionTable")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Date lastUpdate;
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
    private int numComments;
    @ManyToOne
    private User owner;
    @ManyToOne
    private Exercise exercise;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Image solImage;

    public Solution() {
    }

    public Solution(String name, String description, int numComments, Date lastUpdate, User owner) {
        this.name = name;
        this.description = description;
        this.numComments = numComments;
        this.lastUpdate = lastUpdate;
        this.owner = owner;
    }

    public Solution(String name, String description, int numComments, Date lastUpdate, Image solImage, User owner) {
        this.name = name;
        this.description = description;
        this.numComments = numComments;
        this.lastUpdate = lastUpdate;
        this.solImage = solImage;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public int getNumComments() {
        return numComments;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Image getSolImage() {
        return solImage;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public User getOwner() {
        return owner;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }   

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSolImage(Image solImage) {
        this.solImage = solImage;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setSolution(this);
    }

    public String getDescription() {
        return description;
    }

    public void incrementNumComments() {
        this.numComments++;
    }

    public void decrementNumComments() {
        if (this.numComments > 0) {
            this.numComments--;
        }
    }
    

}
