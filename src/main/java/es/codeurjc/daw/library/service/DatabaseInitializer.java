package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.Instant;
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

		User u1 = new User("user", "user@example.com", passwordEncoder.encode("pass"), List.of("USER","ADMIN"), "Bio de user","Especialidad de user", null, new ArrayList<>());
		userImage(u1, "sample_images/u1.png");
		userRepository.save(u1);
	

		
		ExerciseList lista = new ExerciseList("Lista de ejemplo", "Lista para ver", "Algoritmos", new Date(System.currentTimeMillis()), u1, new ArrayList<>());
		exerciseListRepository.save(lista);

		Exercise ex1 = new Exercise("Grafo", "hacer un bfs", 1, u1);
		ex1.setExerciseList(lista);

		Exercise ex2 = new Exercise("Árbol", "hacer un recorrido in-order", 1, u1);
		ex2.setExerciseList(lista);

		exerciseRepository.save(ex1);
		exerciseRepository.save(ex2);

		Solution sol1 = new Solution("Solución al ejercicio de grafo", "Esta es la solución al ejercicio de grafo", 0, new Date(System.currentTimeMillis()), u1);
		sol1.setExercise(ex1);
		setSolutionImage(sol1, "sample_images/dijkstra.jpg");
		solutionRepository.save(sol1);

		Solution sol2 = new Solution("Solución al ejercicio de árbol", "Esta es la solución al ejercicio de árbol in-order", 0, new Date(System.currentTimeMillis()), u1);
		sol2.setExercise(ex2);
		setSolutionImage(sol2, "sample_images/aestrella.jpg");
		solutionRepository.save(sol2);

		Post p1 = new Post(u1, ex1.getTitle(), "/exercise", "New Excercise");
		Post p2 = new Post(u1, ex2.getTitle(), "/exercise", "New Excercise");
		postRepository.save(p1);
		postRepository.save(p2);
	}

	public void userImage(User user, String classpathResource) {
		Resource image = new ClassPathResource(classpathResource);
		try {
			Image createdImage = imageService.createImage(image.getInputStream());
			user.setPhoto(createdImage);

		} catch (IOException e) {
			System.err.println("[DatabaseInitializer] No se pudo cargar la imagen '" + classpathResource + "': " + e.getMessage());
		}
	}
	
	public void setSolutionImage(Solution solution, String classpathResource) {
		Resource image = new ClassPathResource(classpathResource);
		try {
			Image createdImage = imageService.createImage(image.getInputStream());
			solution.setSolImage(createdImage);
		} catch (IOException e) {
			System.err.println("[DatabaseInitializer] No se pudo cargar la imagen '" + classpathResource + "': " + e.getMessage());
		}
	}
}
