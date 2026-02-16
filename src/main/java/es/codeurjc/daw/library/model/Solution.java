package es.codeurjc.daw.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;
import java.sql.Blob;
import java.util.List;

@Entity(name = "SolutionTable")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private int numComments;
    private Date lastUpdate;
    @Lob
    private Blob pdfImage;
    @OneToMany
    private List<Comment> comments;
    @OneToOne
    private User owner;

    public Solution() {
    }

    public Solution(String name, String description, int numComments, Date lastUpdate, User owner) {
        this.name = name;
        this.description = description;
        this.numComments = numComments;
        this.lastUpdate = lastUpdate;
        this.owner = owner;
    }

    public Solution(String name, String description, int numComments, Date lastUpdate, Blob pdfImage, User owner) {
        this.name = name;
        this.description = description;
        this.numComments = numComments;
        this.lastUpdate = lastUpdate;
        this.pdfImage = pdfImage;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNumComments() {
        return numComments;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Blob getPdfImage() {
        return pdfImage;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public User getOwner() {
        return owner;
    }
    
}
