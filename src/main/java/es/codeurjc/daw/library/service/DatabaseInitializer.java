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

		User u1 = null;
		List<User> users = new ArrayList<>();
		for(int i = 1; i <= 20; i++) {
			User user;
			if(i == 1) {
				u1 = new User("user " + i, "user" + i + "@example.com", passwordEncoder.encode("pass"), List.of("USER","ADMIN"), "Bio de user " + i,"Especialidad de user " + i, null, new ArrayList<>());
				user = u1;
			} else {
				user = new User("user " + i, "user" + i + "@example.com", passwordEncoder.encode("pass"), List.of("USER"), "Bio de user " + i,"Especialidad de user " + i, null, new ArrayList<>());
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

		// Follows por defecto para que haya feed útil desde el arranque
		follow(u1, u2);
		follow(u1, u3);
		follow(u1, u4);
		follow(u1, u5);
		follow(u1, u6);

		// Más relaciones entre usuarios para simular red social activa
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
		
		// Contenido del admin/user1
		ExerciseList lista = new ExerciseList("Lista de ejemplo", "Lista para ver", "Algoritmos", new Date(System.currentTimeMillis()), u1, new ArrayList<>());
		exerciseListRepository.save(lista);

		Exercise ex1 = createExerciseWithPost(u1, lista, "Grafo", "Hacer un BFS");
		Exercise ex2 = createExerciseWithPost(u1, lista, "Árbol", "Hacer un recorrido in-order");

		createSolution(ex1, u1, "Solución al ejercicio de grafo", "Esta es la solución al ejercicio de grafo", "sample_images/dijkstra.jpg");
		createSolution(ex2, u1, "Solución al ejercicio de árbol", "Esta es la solución al ejercicio de árbol in-order", "sample_images/aestrella.jpg");

		// Contenido de usuarios seguidos por user1 para poblar el feed
		createUserDemoContent(u2, "Estructuras lineales", "Colas", "Resolver gestión de turnos con cola", "Solución de colas");
		createUserDemoContent(u3, "Recursividad", "Torres de Hanoi", "Resolver n discos con recursividad", "Solución de Hanoi");
		createUserDemoContent(u4, "Grafos avanzados", "Dijkstra", "Camino mínimo en grafo ponderado", "Solución de Dijkstra");
		createUserDemoContent(u5, "Árboles balanceados", "AVL", "Insertar nodos manteniendo balance", "Solución AVL");
		createUserDemoContent(u6, "Hashing", "Tabla hash", "Resolver colisiones por encadenamiento", "Solución de hash");
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
				"Lista de " + owner.getName(),
				"Estructuras de datos",
				new Date(System.currentTimeMillis()),
				owner,
				new ArrayList<>());
		exerciseListRepository.save(list);

		Exercise exercise = createExerciseWithPost(owner, list, exerciseTitle, exerciseDescription);
		createSolution(exercise, owner, solutionTitle, "Esta es la solución propuesta por " + owner.getName(), "sample_images/dijkstra.jpg");
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
