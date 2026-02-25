package es.codeurjc.daw.library.model;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name="FeedPostTable")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User owner;

    private String header, ownerName, description, timeAgo;
    private String contentLink;
    private Instant date;
    private String actionType;

    public Post(){}

    public Post(User owner, String header, String contentLink, String actionType){
        this.owner = owner;
        this.actionType = actionType;
        this.header = header;
        this.ownerName = owner.getName();
        this.contentLink = contentLink;
        this.date = Instant.now();
        this.timeAgo = "";
    }

    public void calculateTime(){
        Duration duration = Duration.between(this.date, Instant.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            this.timeAgo = minutes + " minutes ago";
        } 
        else if (hours < 24) {
            this.timeAgo = hours + " hours ago";
        } 
        else {
            this.timeAgo = days + " days ago";
        }
    }


    public User getOwner(){
        return this.owner;
    }

    public String getActionType(){
        return this.actionType;
    }

    public String getHeader(){
        return this.header;
    }

    public String getOwnerName(){
        return this.ownerName;
    }

    public String getDescription(){
        return this.description;
    }

    public String getContentLink(){
        return this.contentLink;
    }

    public Instant geDate(){
        return this.date;
    }

    public String getTimeAgo(){
        return this.timeAgo;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }
}
