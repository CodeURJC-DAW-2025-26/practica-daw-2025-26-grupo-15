package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import java.util.List;
import jakarta.persistence.CascadeType;

import java.sql.Date;


@Entity(name = "ExerciseListTable")
public class ExerciseList  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String topic;
    private Date lastUpdate;

    @ManyToOne
    private User owner;
    @OneToMany(mappedBy = "exerciseList", cascade = CascadeType.ALL)
    private List<Exercise> exercises;

    public ExerciseList() {
    }

    public ExerciseList(String title, String description, String topic, Date lastUpdate, User owner, List<Exercise> exercises) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.lastUpdate = lastUpdate;
        this.owner = owner;
        this.exercises = exercises;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTopic() {
        return topic;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public User getOwner() { 
        return owner; 
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

}
