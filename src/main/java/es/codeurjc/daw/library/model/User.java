package es.codeurjc.daw.library.model;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity(name = "UserTable")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String email;
	private String encodedPassword;
	private String name;
	private String bio;
	private String specialty;
	private int followersNumber;
	private int followingNumber;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Image photo;
	
	@ManyToMany
	@JoinTable(
		name = "user_following",
		joinColumns = @JoinColumn(name = "follower_id"),
		inverseJoinColumns = @JoinColumn(name = "followed_id")
	)
	private Set<User> following = new HashSet<>();

	@ManyToMany
	private List<User> requestedFriends;
	@ManyToMany(mappedBy = "requestedFriends")
	private List<User> requestReceived;

    private String provider;    // "local", "google"
    private String providerId;
	

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<ExerciseList> exerciseLists;
	
	@OneToMany(mappedBy = "owner")
	private List<Post> posts;

	

	public User() {}

	public User(String name, String email, String encodedPassword) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = List.of("USER");
		this.bio = "";
		this.specialty = "";
		this.photo = null;
		this.followersNumber = 0;
		this.followingNumber = 0;
		this.exerciseLists = List.of();
		this.provider = "local";
		this.providerId = "";
		this.posts = List.of();
		this.following = Set.of();
	}

	public User(String name, String email, String encodedPassword, List<String> roles, String bio, String specialty,
				Image photo, int followers, int following, List<ExerciseList> exerciseLists) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = roles;
		this.bio = bio;
		this.specialty = specialty;
		this.photo = photo;
		this.followersNumber = followers;
		this.followingNumber = following;
		this.exerciseLists = exerciseLists;
		this.provider = "local";
		this.providerId = "";
		this.posts = List.of();
		this.following = Set.of();
	}

	public void addPost(Post p){
		this.posts.add(p);
		p.setOwner(this);
	}



	public int getFollowersNumber() {
		return followersNumber;
	}
	public int getFollowingNumber() {
		return followingNumber;
	}
	public void setFollowingNumber(int following) {
		this.followingNumber = following;
	}

	public void setRequestReceived(List<User> requestReceived) {
		this.requestReceived = requestReceived;
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


	
    public List<User> getRequestReceived() {
        return requestReceived;
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

    public void setFollowersNumber(int followers) {
        this.followersNumber = followers;
    }	

	public String getBio() {
		return bio;
	}

	public String getSpecialty() {
		return specialty;
	}

	public Image getPhoto() {
		return photo;
	}

	public String getNameInitial() {
		return (name != null && !name.isEmpty()) ? String.valueOf(name.charAt(0)).toUpperCase() : "?";
	}

	public void setPhoto(Image photo) {
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

	public Set<User> getFollowing(){
		return this.following;
	}
}