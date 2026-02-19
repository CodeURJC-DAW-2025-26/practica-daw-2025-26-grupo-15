package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import java.sql.Blob;
import java.util.List;

@Entity(name = "ExerciseTable")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private int numSolutions;
    @Lob
    private Blob pdfImage;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<Solution> solutions;
    @ManyToOne
    private User owner;
    @ManyToOne
    private ExerciseList exerciseList;


    public Exercise() {
    }

    //constructor sin imagen pdf porque el usuario tiene la opción de subirlo así
    public Exercise(String title, String description, int numSolutions, User owner) {
        this.title = title;
        this.description = description;
        this.numSolutions = numSolutions;
        this.owner = owner;
    }

    public Exercise(String title, String description, int numSolutions, Blob pdfImage, User owner) {
        this.title = title;
        this.description = description;
        this.numSolutions = numSolutions;
        this.pdfImage = pdfImage;
        this.owner = owner;
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

    public int getNumSolutions() {
        return numSolutions;
    }

    public Blob getPdfImage() {
        return pdfImage;
    }

    public void setExerciseList(ExerciseList exerciseList) {
        this.exerciseList = exerciseList;
    }

    public ExerciseList getExerciseList() {
        return exerciseList;
    }

    public User getOwner() {
        return owner;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }
}
