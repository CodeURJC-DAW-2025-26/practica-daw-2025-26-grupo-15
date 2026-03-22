package es.codeurjc.daw.library.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.dto.FollowingSuggestionDTO;
import es.codeurjc.daw.library.dto.FollowingSuggestionMapper;
import es.codeurjc.daw.library.dto.ImageMapper;
import es.codeurjc.daw.library.dto.UserBasicInfoDTO;
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
    @Autowired
    private FollowingSuggestionMapper followingSuggestionMapper;

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userMapper.toDTO(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(HttpServletRequest request, @PathVariable Long id){
        try{
            User user = userService.getUser(request.getUserPrincipal().getName());
            boolean isAdmin = request.isUserInRole("ADMIN");
            User deletedUser = userService.deleteUser(user,id,isAdmin);

        
            if (user.getId() == id) {
                    request.getSession().invalidate();
            }

            return ResponseEntity.ok(userMapper.toDTO(deletedUser));
        }
        catch(SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
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
    public Page<UserDTO> getUsers(Pageable pageable,
                                  @RequestParam(required = false) Long excludedId,
                                  @RequestParam(required = false) String nameFilter){

        Page<User> usersPage = searchService.searchUsers(pageable, nameFilter, excludedId);
        
        if (usersPage == null) throw new RuntimeException("Unable to find users page");
        Page<UserDTO> usersDTOPage = usersPage.map(userMapper::toDTO);
        
        return usersDTOPage;
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody UserLoginDTO userLogDto) {
        try{
            User user = userMapper.toEntity(userLogDto);
            User createdUser = userService.register(user);
            UserDTO userDTO = userMapper.toDTO(createdUser);
            
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();

            return ResponseEntity.created(location).body(userDTO);
        }

        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putUser(@PathVariable Long id,Principal principal ,@RequestBody UserEditDTO userEditDto) {
        try{
            User currentUser = userService.getUser(principal.getName());
            User updatedUser = userMapper.fromUserEditDTOtoEntity(userEditDto);
            updatedUser.setId(id); // ask
            User modifiedUser = userService.modify(updatedUser,currentUser);
            return ResponseEntity.ok(userMapper.toDTO(modifiedUser));

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", e.getMessage()));
        }
        catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    
    }


    @PostMapping("/{targetId}/follow-requests/")
    public ResponseEntity<?> sendFollowRequest(Principal principal, @PathVariable Long targetId) {
        try{
              User requester = userService.getUser(principal.getName());
            User target = userService.getUser(targetId);
            userService.requestToFollow(requester, target);
            return ResponseEntity.ok(Map.of("message", "Follow request sent successfully"));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/follow-requests/")
    public ResponseEntity<?> getFollowRequests(Principal principal) {
        try {
            if (principal == null) throw new SecurityException("User not authenticated");
            User user = userService.getUser(principal.getName());
            List<UserBasicInfoDTO> followRequests = userMapper.toBasicInfoDTOs(user.getRequestReceived());
            return ResponseEntity.ok(followRequests);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }       
    }

    @DeleteMapping("/me/follows/{targetId}")
    public ResponseEntity<?> unfollowUser(Principal principal, @PathVariable Long targetId){
        try{
            User requester = userService.getUser(principal.getName());
            User target = userService.getUser(targetId);
            userService.unfollow(requester, target);
            UserBasicInfoDTO unfollowed = userMapper.toBasicInfoDTO(target);
            return ResponseEntity.ok(unfollowed);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/follows/")
    public ResponseEntity<?> getFollowing(Principal principal) {
        try{
            User user = userService.getUser(principal.getName());
            List<UserBasicInfoDTO> following = userMapper.toBasicInfoDTOs(user.getFollowing());
            return ResponseEntity.ok(following);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/followers/")
    public ResponseEntity<?> getFollowers(Principal principal) {
        try{
            User user = userService.getUser(principal.getName());
            List<UserBasicInfoDTO> followers = userMapper.toBasicInfoDTOs(user.getFollowers());
            return ResponseEntity.ok(followers);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @DeleteMapping("/me/followers/{followerId}")
    public ResponseEntity<?> removeFollower(Principal principal, @PathVariable Long followerId){
        try{
            User user = userService.getUser(principal.getName());
            User follower = userService.getUser(followerId);
            userService.unfollow(follower, user);
            UserBasicInfoDTO removed  = userMapper.toBasicInfoDTO(follower);
            return ResponseEntity.ok(removed);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }


    //Ask
    @PostMapping("/me/follow-requests/{requesterId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requesterId, Principal principal){
        try{
            User currentUser = userService.getUser(principal.getName());
            userService.acceptFollowRequest(currentUser,requesterId);
            return ResponseEntity.ok(Map.of("message", "Request accepted successfully"));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me/follow-requests/{requesterId}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requesterId, Principal principal){
        try{
            User currentUser = userService.getUser(principal.getName());
            userService.declineFollowRequest(currentUser,requesterId);
            return ResponseEntity.ok(Map.of("message", "Request rejected successfully"));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/me/following-suggestions/")
    public ResponseEntity<?> getFollowingSuggestions(Principal principal){
        try{
            User user = userService.getUser(principal.getName());
            List<FollowingSuggestionDTO> suggestions = followingSuggestionMapper
                .toDTOs(userService.getFollowingSuggestions(user));
            return ResponseEntity.ok(suggestions);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
        
    }

    @GetMapping("/{id}/number-of-followers")
    public ResponseEntity<?> getNumberOfFollowers(@PathVariable Long id) {
        try{
            User user = userService.getUser(id);
            long count = user.getFollowers().size();
            return ResponseEntity.ok(Map.of("number-of-followers", count));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/number-of-following")
    public ResponseEntity<?> getNumberOfFollowing(@PathVariable Long id) {
        try{
            User user = userService.getUser(id);
            long count = user.getFollowing().size();
            return ResponseEntity.ok(Map.of("number-of-following", count));
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
}
