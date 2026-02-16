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

	@OneToMany(cascade = CascadeType.ALL)
	private List<ExerciseList> excerciseLists;

	

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
		this.excerciseLists = exerciseLists;
		
	}

	public Long getId() {
		return id;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getName() {
		return name;
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

	public int getFollowers() {
		return followers;
	}

	public int getFollowing() {
		return following;
	}

}