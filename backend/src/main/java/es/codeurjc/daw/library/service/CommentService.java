package es.codeurjc.daw.library.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.repository.CommentRepository;
import es.codeurjc.daw.library.model.Comment;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.model.Solution;
import java.sql.Date;
import java.util.NoSuchElementException;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private SolutionService solutionService;

    public Comment createComment(Comment comment, User user, Solution solution) {
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        comment.setOwner(user);
        comment.setSolution(solution);
        comment.getSolution().incrementNumComments();
        comment.setLastUpdate(new Date(System.currentTimeMillis()));
        solution.getComments().add(comment);
        return commentRepo.save(comment);
    }

    public Comment deleteComment(Long commentId, User user, boolean isAdmin) {
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getOwner().getId().equals(user.getId()) && !isAdmin) {
            throw new RuntimeException("You do not have permission to delete this comment");
        }
        comment.getSolution().decrementNumComments();
        commentRepo.delete(comment);
        return comment;
    }

    public Comment getById(Long id){
        return commentRepo.findById(id).orElseThrow(() -> new NoSuchElementException());
    }

}
