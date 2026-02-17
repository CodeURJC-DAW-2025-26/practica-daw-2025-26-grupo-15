package es.codeurjc.daw.library.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Book;
import es.codeurjc.daw.library.model.Exercise;
import es.codeurjc.daw.library.model.ExerciseList;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ExerciseListRepository;
import es.codeurjc.daw.library.repository.ExerciseRepository;
import es.codeurjc.daw.library.repository.PostRepository;
import es.codeurjc.daw.library.repository.UserRepository;

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
	private PostRepository postRepository; 

	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		User u1 = new User("user","user@example.com", passwordEncoder.encode("pass"), List.of("USER"), "Bio de user", "Especialidad de user", "img", 100, 150, null);
		userRepository.save(u1);
		Exercise ex1 = new Exercise("Grafo", "hacer un bfs", 0, u1);
		Exercise ex2 = new Exercise("Árbol", "hacer un recorrido in-order", 0, u1);

		exerciseRepository.save(ex1);
		exerciseRepository.save(ex2);

		List<Exercise> ejercicios = new ArrayList<>();
		ejercicios.add(ex1);
		ejercicios.add(ex2);

		ExerciseList lista = new ExerciseList("Lista de ejemplo", "Lista para ver", "16/02", u1, ejercicios);
		exerciseListRepository.save(lista);

		ex1.setExerciseList(lista); 
		ex2.setExerciseList(lista);

		exerciseRepository.save(ex1);
		exerciseRepository.save(ex2);

		Post p1 = new Post(u1, ex1.getTitle(),"/exercise", "New Excercise");
		Post p2 = new Post(u1, ex2.getTitle(),"/exercise", "New Excercise");

		postRepository.save(p1);
		postRepository.save(p2);
	}

	public void setBookImage(Book book, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);

		Image createdImage = imageService.createImage(image.getInputStream());
		book.setImage(createdImage);
	}
}
