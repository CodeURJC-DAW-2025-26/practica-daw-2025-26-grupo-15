package es.codeurjc.daw.library.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public Page<PostDTO> getLists(@RequestParam(required = true ) int page,
                                          @RequestParam(required = true) int size,
                                          @RequestParam Long currentUserId){
        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");

        Page<Post> postsPage = searchService.searchPosts(page, size, null, currentUserId);
        
        if (postsPage == null) throw new RuntimeException("Unable to find posts page");
        Page<PostDTO> postsDTOPage = postsPage.map(postMapper::toDTO);
        
        return postsDTOPage;
    }
    
}
