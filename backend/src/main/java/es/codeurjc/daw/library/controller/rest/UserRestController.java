package es.codeurjc.daw.library.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.dto.ImageMapper;
import es.codeurjc.daw.library.dto.UserDTO;
import es.codeurjc.daw.library.dto.UserEditDTO;
import es.codeurjc.daw.library.dto.UserLoginDTO;
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.service.SearchService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired 
    private UserMapper userMapper;
    @Autowired 
    private UserService userService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userMapper.toDTO(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(HttpServletRequest request, @PathVariable Long id){
        User user = userService.getUser(request.getUserPrincipal().getName());
        boolean isAdmin = request.isUserInRole("ADMIN");
        User deletedUser = userService.deleteUser(user,id,isAdmin);

        
        if (user.getId() == id) {
                request.getSession().invalidate();
        }

        return userMapper.toDTO(deletedUser);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserLoginDTO userLogDto) {
        User user = userMapper.toEntity(userLogDto);
        User createdUser = userService.register(user);
        UserDTO userDTO = userMapper.toDTO(createdUser);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();

        return ResponseEntity.created(location).body(userDTO);
        
    }

    @PutMapping("/{id}")
    public UserDTO putUser(@PathVariable Long id,Principal principal ,@RequestBody UserEditDTO userEditDto) {
        User currentUser = userService.getUser(principal.getName());
        User updatedUser = userMapper.fromUserEditDTOtoEntity(userEditDto);
        User modifiedUser = userService.modify(updatedUser,currentUser);
        return userMapper.toDTO(modifiedUser);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> createUserImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile,
            Principal principal){

        try{
            
            User oldUser = userService.getUser(principal.getName());
            User editedUser = userService.addPhotoToUser(id, oldUser, imageFile);
            
            URI location = fromCurrentContextPath()
                    .path("/api/v1/images/{imageId}/media")
                    .buildAndExpand(editedUser.getPhoto().getId())
                    .toUri();

            return ResponseEntity.created(location).body(imageMapper.toDTO(editedUser.getPhoto()));
        }
        catch(SecurityException e){
            String errorMessage = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Forbidden"
                    : e.getMessage();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", errorMessage));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    
    }

    @GetMapping("/me")
    public UserDTO getUserLogged(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if(principal != null){
            User user = userService.getUser(principal.getName());
            return userMapper.toDTO(user);
        }
        else{
            throw new NoSuchElementException();
        }
    }


    @GetMapping("/")
    public Page<UserDTO> getUsers(@RequestParam(required = true) int page,
                                  @RequestParam(required = true) int size,
                                  @RequestParam Long excludeId,
                                  @RequestParam String nameFilter){

        if (page < 0 || size < 0) throw new IllegalArgumentException("Invalid page or size");
        Page<User> usersPage = searchService.searchUsers(page, size, nameFilter, excludeId);
        
        if (usersPage == null) throw new RuntimeException("Unable to find users page");
        Page<UserDTO> usersDTOPage = usersPage.map(userMapper::toDTO);
        
        return usersDTOPage;
    }
    
}
