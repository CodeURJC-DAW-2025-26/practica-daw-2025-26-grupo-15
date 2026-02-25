package es.codeurjc.daw.library.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.CommentRepository;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Solution;
import java.sql.Date;

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
        comment.getSolution().incrementNumComments();
        comment.setLastUpdate(new Date(System.currentTimeMillis()));
        solution.getComments().add(comment);
        commentRepo.save(comment);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this comment");
        }
        comment.getSolution().decrementNumComments();
        commentRepo.delete(comment);
    }
}
