package es.codeurjc.daw.library.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.PostDTO;
import es.codeurjc.daw.library.dto.PostMapper;
import es.codeurjc.daw.library.model.Post;
import es.codeurjc.daw.library.service.SearchService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    @Autowired private PostMapper postMapper;
    @Autowired private SearchService searchService;

    @GetMapping("/")
    public Page<PostDTO> getPosts(Pageable pageable,
                                  @RequestParam(required = false) Long currentUserId){

        Page<Post> postsPage = searchService.searchPosts(pageable, null, currentUserId);
        
        if (postsPage == null) throw new RuntimeException("Unable to find posts page");
        Page<PostDTO> postsDTOPage = postsPage.map(postMapper::toDTO);
        
        return postsDTOPage;
    }
    
}
