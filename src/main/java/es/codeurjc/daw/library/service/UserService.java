package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.User;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired 
    private UserRepository userRepo;

    @Autowired
    private ImageService imageService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public Optional<User> findByName(String name){
        return userRepo.findByName(name);
    }

    public Optional<User> findByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId){
        return userRepo.findByProviderAndProviderId(provider, providerId);
    }

    public boolean existsByName(String name) {
        return userRepo.findByName(name).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public java.util.List<User> findAll() {
        return userRepo.findAll();
    }

    public void requestToFollow(User requester, User target){
        if (requester.getId().equals(target.getId())) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
        
        if (requester.getRequestedFriends().contains(target)){
            throw new IllegalArgumentException("Follow request already sent");
        }

        if (requester.getFollowing().contains(target)){
            throw new IllegalArgumentException("Already following this user");
        }

        requester.getRequestedFriends().add(target);
        target.getRequestReceived().add(requester);
        userRepo.save(requester);
        userRepo.save(target);
    }

    public boolean hasRequestedToFollow(User requester, User target){
        return requester.getRequestedFriends().contains(target);
    }

    public User register(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Username cannot be null or empty.");
        }
        if(user.getName().length() < 3 || user.getName().length() > 30){
            throw new RuntimeException("Username must be between 3 and 30 characters.");
        }
        if(user.getName().matches(".*[^a-zA-Z0-9_].*")){
            throw new RuntimeException("Username can only contain letters, numbers and underscores.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email cannot be null or empty.");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format.");
        }
        if(user.getEncodedPassword() == null || user.getEncodedPassword().trim().isEmpty()){
            throw new RuntimeException("Password cannot be null or empty.");
        }
        if(user.getEncodedPassword().length() < 8 || user.getEncodedPassword().length() > 64){
            throw new RuntimeException("Password must be between 8 and 64 characters long.");
        }
        if(user.getEncodedPassword().matches(".*\\s.*")){
            throw new RuntimeException("Password cannot contain whitespace.");
        }
        if (!user.getEncodedPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#_\\-]).{8,64}$")) {
            throw new RuntimeException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
        if (existsByName(user.getName())) {
            throw new RuntimeException("Username '" + user.getName() + "' is already taken.");
        }

        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email '" + user.getEmail() + "' is already registered.");
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        
        return userRepo.save(user);
    }

    public Slice<User> searchUsersBySimilarName(String q, int page, int size) {
        return userRepo.searchUsersBySimilarName(q, PageRequest.of(page, size));
    }

    public User modify(User user, User oldUser) {
        return modify(user, oldUser, null);
    }

    public User modify(User user, User oldUser, MultipartFile photoFile) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        oldUser.setName(user.getName());
        oldUser.setBio(user.getBio());
        oldUser.setSpecialty(user.getSpecialty());

        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                if (oldUser.getPhoto() != null) {
                    Image updated = imageService.replaceImageFile(oldUser.getPhoto().getId(), photoFile.getInputStream());
                    oldUser.setPhoto(updated);
                } else {
                    Image newImage = imageService.createImage(photoFile.getInputStream());
                    oldUser.setPhoto(newImage);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile photo", e);
            }
        }

        return userRepo.save(oldUser);
    }
}
