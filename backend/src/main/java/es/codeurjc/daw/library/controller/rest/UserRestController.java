package es.codeurjc.daw.library.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.dto.UserDTO;
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired private UserMapper userMapper;
    @Autowired private UserService userService;
    @Autowired private SearchService searchService;

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userMapper.toDTO(userService.findById(id).orElseThrow(() -> new IllegalArgumentException("no user for this id")));
    }

    @GetMapping("/")
    public Page<UserDTO> getUsers(@RequestParam(required = true) int page,
                                  @RequestParam(required = true) int size,
                                  @RequestParam Long excludedId,
                                  @RequestParam String nameFilter){

        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");
        Page<User> usersPage = searchService.searchUsers(page, size, nameFilter, excludedId);
        
        if (usersPage == null) throw new RuntimeException("Unable to find users page");
        Page<UserDTO> usersDTOPage = usersPage.map(userMapper::toDTO);
        
        return usersDTOPage;
    }
    
}
