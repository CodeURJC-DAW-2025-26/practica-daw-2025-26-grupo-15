package es.codeurjc.daw.library.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.PostRepository;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class PostService {
 
    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;

    public List<Post> findAll(){
        return postRepo.findAll();
    }


    public Post createPost(Post newPost){
        newPost.getOwner().addPost(newPost);
        newPost.setDate(Instant.now());
        newPost.calculateTime();
        postRepo.save(newPost);
        return newPost;
    }

    public Post deletePost(Long postId, User requester, boolean isAdmin) {
        Post post = postRepo.findById(postId).orElseThrow();

        if (!post.getOwner().getId().equals(requester.getId()) && !isAdmin) {
            throw new SecurityException("You don't have permission to delete this post");
        }

        requester.removePost(post);
        postRepo.delete(post);
        userRepo.save(requester);
        return post;
    }

    public Page<Post> findFeedForUser(User user, Pageable pageable){
        return postRepo.findFeedForUser(user.getId(), pageable);
    }

    public Page<Post> findAll(Pageable pageable){
        return postRepo.findAll(pageable);
    }

    public Post getPost(Long postId) {
        return postRepo.findById(postId).orElseThrow();
    }

}
