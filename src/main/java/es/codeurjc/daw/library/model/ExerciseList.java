package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import java.util.List;

@Entity(name = "ExerciseListTable")
public class ExerciseList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String lastUpdate;
    @ManyToOne
    private User owner;
    @OneToMany
    private List<Exercise> exercises;

    public ExerciseList() {
    }

    public ExerciseList(String title, String description, String lastUpdate, User owner, List<Exercise> exercises) {
        this.title = title;
        this.description = description;
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public User getOwner() { 
        return owner; 
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

}
