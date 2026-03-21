package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ExerciseListRepository;
import es.codeurjc.daw.library.repository.ExerciseRepository;
import es.codeurjc.daw.library.repository.PostRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.model.Solution;
import es.codeurjc.daw.library.repository.SolutionRepository;

@Service
public class DatabaseInitializer {

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ExerciseListRepository exerciseListRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ExerciseRepository exerciseRepository;

	@Autowired
	private SolutionRepository solutionRepository;

	@Autowired
	private PostRepository postRepository;

	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		if(userRepository.count() > 0) {
			return;
		}

		User u1 = null;
		List<User> users = new ArrayList<>();
		for(int i = 1; i <= 20; i++) {
			User user;
			if(i == 1) {
				u1 = new User("user" + i, "user" + i + "@example.com", passwordEncoder.encode("pass"), List.of("USER","ADMIN"), "Bio of user " + i,"Specialty of user " + i, null, new ArrayList<>());
				user = u1;
			} else {
				user = new User("user" + i, "user" + i + "@example.com", passwordEncoder.encode("pass"), List.of("USER"), "Bio of user " + i,"Specialty of user " + i, null, new ArrayList<>());
			}
			userImage(user, "sample_images/u1.png");
			userRepository.save(user);
			users.add(user);
		}

		User u2 = users.get(1);
		User u3 = users.get(2);
		User u4 = users.get(3);
		User u5 = users.get(4);
		User u6 = users.get(5);

		// Default follows so the feed is useful on startup
		follow(u1, u2);
		follow(u1, u3);
		follow(u1, u4);
		follow(u1, u5);
		follow(u1, u6);

		// More user relationships to simulate an active social network
		follow(u2, u1);
		follow(u3, u1);
		follow(u4, u1);
		follow(u2, u3);
		follow(u2, u4);
		follow(u3, u4);
		follow(u3, u5);
		follow(u4, u5);
		follow(u5, u6);

		userRepository.saveAll(List.of(u1, u2, u3, u4, u5, u6));
		
		// Initial content for admin/user1
		ExerciseList sampleList = new ExerciseList("Sample List", "List to browse", "Algorithms", new Date(System.currentTimeMillis()), u1, new ArrayList<>());
		exerciseListRepository.save(sampleList);

		Exercise ex1 = createExerciseWithPost(u1, sampleList, "Graph", "Perform a BFS");
		Exercise ex2 = createExerciseWithPost(u1, sampleList, "Tree", "Perform an in-order traversal");

		createSolution(ex1, u1, "Solution to the graph exercise", "This is the solution to the graph exercise", "sample_images/dijkstra.jpg");
		createSolution(ex2, u1, "Solution to the tree exercise", "This is the solution to the in-order tree exercise", "sample_images/aestrella.jpg");

		// Content from users followed by user1 to populate the feed
		createUserDemoContent(u2, "Linear Data Structures", "Queues", "Solve queue-based turn management", "Queue solution");
		createUserDemoContent(u3, "Recursion", "Towers of Hanoi", "Solve n disks with recursion", "Hanoi solution");
		createUserDemoContent(u4, "Advanced Graphs", "Dijkstra", "Shortest path in a weighted graph", "Dijkstra solution");
		createUserDemoContent(u5, "Balanced Trees", "AVL", "Insert nodes while keeping balance", "AVL solution");
		createUserDemoContent(u6, "Hashing", "Hash Table", "Resolve collisions with chaining", "Hash solution");

		// Extra real content for infinite scroll tests (focus on user1)
		createBulkContentForUser(u1, "Admin", 10, 3);
		createBulkContentForUser(u2, "Queue", 3, 2);
		createBulkContentForUser(u3, "Recursion", 3, 2);
		createBulkContentForUser(u4, "Graph", 3, 2);
		createBulkContentForUser(u5, "Tree", 3, 2);
		createBulkContentForUser(u6, "Hash", 3, 2);
	}

	private void follow(User follower, User followed) {
		if (follower == null || followed == null || follower.equals(followed)) {
			return;
		}
		if (!follower.getFollowing().contains(followed)) {
			follower.getFollowing().add(followed);
		}
		if (!followed.getFollowers().contains(follower)) {
			followed.getFollowers().add(follower);
		}
	}

	private void createUserDemoContent(User owner, String listTitle, String exerciseTitle, String exerciseDescription, String solutionTitle) {
		ExerciseList list = new ExerciseList(
				listTitle,
				"List of " + owner.getName(),
				"Data Structures",
				new Date(System.currentTimeMillis()),
				owner,
				new ArrayList<>());
		exerciseListRepository.save(list);

		Exercise exercise = createExerciseWithPost(owner, list, exerciseTitle, exerciseDescription);
		createSolution(exercise, owner, solutionTitle, "This is the proposed solution by " + owner.getName(), "sample_images/dijkstra.jpg");
	}

	private Exercise createExerciseWithPost(User owner, ExerciseList list, String title, String description) {
		Exercise exercise = new Exercise(title, description, 0, owner);
		exercise.setExerciseList(list);
		exerciseRepository.save(exercise);

		Post post = new Post(owner, exercise.getTitle(), "/exercise/" + exercise.getId(), "New Exercise");
		postRepository.save(post);
		return exercise;
	}

	private void createSolution(Exercise exercise, User owner, String title, String description, String imagePath) {
		Solution solution = new Solution(title, description, 0, new Date(System.currentTimeMillis()), owner);
		solution.setExercise(exercise);
		setSolutionImage(solution, imagePath);
		solutionRepository.save(solution);
		exercise.incrementNumSolutions();
		exerciseRepository.save(exercise);
	}

	private void createBulkContentForUser(User owner, String topicPrefix, int listCount, int exercisesPerList) {
		for (int i = 1; i <= listCount; i++) {
			ExerciseList list = new ExerciseList(
					topicPrefix + " List " + i,
					"Practice list " + i + " by " + owner.getName(),
					topicPrefix + " Topic " + i,
					new Date(System.currentTimeMillis()),
					owner,
					new ArrayList<>());
			exerciseListRepository.save(list);

			for (int j = 1; j <= exercisesPerList; j++) {
				String title = topicPrefix + " Exercise " + i + "." + j;
				String description = "Solve the " + topicPrefix.toLowerCase() + " challenge " + i + "." + j;
				Exercise exercise = createExerciseWithPost(owner, list, title, description);
				createSolution(
						exercise,
						owner,
						"Solution " + i + "." + j,
						"Detailed answer for " + title + " by " + owner.getName(),
						"sample_images/dijkstra.jpg");
			}
		}
	}

	public void userImage(User user, String classpathResource) {
		Resource image = new ClassPathResource(classpathResource);
		try {
			Image createdImage = imageService.createImage(image.getInputStream());
			user.setPhoto(createdImage);

		} catch (IOException e) {
			System.err.println("[DatabaseInitializer] Could not load image '" + classpathResource + "': " + e.getMessage());
		}
	}
	
	public void setSolutionImage(Solution solution, String classpathResource) {
		Resource image = new ClassPathResource(classpathResource);
		try {
			Image createdImage = imageService.createImage(image.getInputStream());
			solution.setSolImage(createdImage);
		} catch (IOException e) {
			System.err.println("[DatabaseInitializer] Could not load image '" + classpathResource + "': " + e.getMessage());
		}
	}
}
