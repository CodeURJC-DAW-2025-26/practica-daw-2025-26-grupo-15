package es.codeurjc.daw.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.PostRepository;
import jakarta.transaction.Transactional;

@Service
public class PostService {
 
    @Autowired
    private PostRepository postRepo;

    public List<Post> findAll(){
        return postRepo.findAll();
    }

    @Transactional
    public Post createPost(Post newPost){
        newPost.getOwner().addPost(newPost);
        postRepo.save(newPost);
        return newPost;
    }

    public Slice<Post> findFeedForUser(User user, int page, int size){
        return postRepo.findFeedForUser(user.getId(), PageRequest.of(page, size));
    }

    public Slice<Post> findAll(int page, int size){
        return postRepo.findAll(PageRequest.of(page, size));
    }


}
