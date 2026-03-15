package es.codeurjc.daw.library.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.UserDTO;
import es.codeurjc.daw.library.dto.UserEditDTO;
import es.codeurjc.daw.library.dto.UserLoginDTO;
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.model.User;
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
    UserMapper userMapper;

    @Autowired
    UserService userService;


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

    
    
}
