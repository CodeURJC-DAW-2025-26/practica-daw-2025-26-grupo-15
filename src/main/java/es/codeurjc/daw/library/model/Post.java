package es.codeurjc.daw.library.model;

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

    private String header, ownerName, description;
    private String contentLink;
    private String actionType;

    public Post(){}

    public Post(User owner, String header, String contentLink, String actionType){
        this.owner = owner;
        this.actionType = actionType;
        this.header = header;
        this.ownerName = owner.getName();
        this.contentLink = contentLink;
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
}
