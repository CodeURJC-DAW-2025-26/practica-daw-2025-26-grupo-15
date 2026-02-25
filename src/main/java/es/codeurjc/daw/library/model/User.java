package es.codeurjc.daw.library.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String email;
	private String name;
	private String encodedPassword;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;
	private String bio;
	private String specialty;
	private String photo;	

	@ManyToMany(mappedBy = "followers")
	private List<User> following;
 
	@ManyToMany
	private List<User> followers;

	@ManyToMany(mappedBy = "requestedFriends")
	private List<User> requestRecieved;

	@ManyToMany
	private List<User> requestedFriends;

    private String provider;    // "local", "google"
    private String providerId;
	

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<ExerciseList> exerciseLists;
	

	

	public User() {}

	public User(String name, String email, String encodedPassword) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = List.of("USER");
		this.bio = "";
		this.specialty = "";
		this.photo = "";
		this.followers = new ArrayList<>();
		this.following = new ArrayList<>();
		this.requestRecieved = new ArrayList<>();
		this.requestedFriends = new ArrayList<>();
		this.exerciseLists = new ArrayList<>();
		this.provider = "local";
		this.providerId = "";
	}

	public User(String name, String email, String encodedPassword, List<String> roles, String bio, String specialty,
				String photo, List<User> followers, List<User> following, List<ExerciseList> exerciseLists) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = roles;
		this.bio = bio;
		this.specialty = specialty;
		this.photo = photo;
		this.followers = followers;
		this.following = following;
		this.exerciseLists = exerciseLists;
		this.provider = "local";
		this.providerId = "";
		
	}
	public int getSizeFollowers() {
		return followers.size();
	}
	public int getSizeFollowing() {
		return following.size();
	}
	public void setFollowing(List<User> following) {
		this.following = following;
	}

	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}

	public void setRequestRecieved(List<User> requestRecieved) {
		this.requestRecieved = requestRecieved;
	}

	public void setRequestedFriends(List<User> requestedFriends) {
		this.requestedFriends = requestedFriends;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public List<User> getRequestRecieved() {
        return requestRecieved;
    }

    public List<User> getRequestedFriends() {
        return requestedFriends;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }    
	

	public String getBio() {
		return bio;
	}

	public String getSpecialty() {
		return specialty;
	}

	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getProvider() { 
		return provider; 
	}
    public void setProvider(String provider) {
		 this.provider = provider; 
	}

    public String getProviderId() { 
		return providerId; 
	}
    public void setProviderId(String providerId) { 
		this.providerId = providerId; 
	}
	public List<ExerciseList> getExerciseLists() {
		return exerciseLists;
	}
	public void setExerciseLists(List<ExerciseList> exerciseLists) {
		this.exerciseLists = exerciseLists;
	}
}