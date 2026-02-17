package es.codeurjc.daw.library.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
	private String speciality;
	private String photo;
	private int followers;
	private int following;	

    private String provider;    // "local", "google"
    private String providerId;
	

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<ExerciseList> exerciseLists;
	

	

	public User() {}

	public User(String name, String email, String encodedPassword, List<String> roles, String bio, String speciality,
				String photo, int followers, int following, List<ExerciseList> exerciseLists) {
		this.name = name;
		this.email = email;
		this.encodedPassword = encodedPassword;
		this.roles = roles;
		this.bio = bio;
		this.speciality = speciality;
		this.photo = photo;
		this.followers = followers;
		this.following = following;
		this.exerciseLists = exerciseLists;
		this.provider = "local";
		this.providerId = "";
		
	}
	public int getFollowers() {
		return followers;
	}
	public int getFollowing() {
		return following;
	}
	public void setFollowing(int following) {
		this.following = following;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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

    public void setFollowers(int followers) {
        this.followers = followers;
    }	

	public String getBio() {
		return bio;
	}

	public String getSpeciality() {
		return speciality;
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
}