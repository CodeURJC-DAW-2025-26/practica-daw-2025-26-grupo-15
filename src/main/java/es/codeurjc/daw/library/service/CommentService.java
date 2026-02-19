package es.codeurjc.daw.library.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.CommentRepository;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Solution;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepo;

    public void createComment(Comment comment, User user, Solution solution) {
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        comment.setOwner(user);
        comment.setSolution(solution);
        comment.setLastUpdate(java.time.LocalDateTime.now());
        solution.getComments().add(comment);
        commentRepo.save(comment);
    }
}
