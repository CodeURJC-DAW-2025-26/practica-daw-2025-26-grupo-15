package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired 
    private UserRepository userRepo;

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
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getBio() == null || user.getBio().isEmpty()) {
            throw new IllegalArgumentException("Bio cannot be empty");
        }

        if (user.getSpecialty() == null || user.getSpecialty().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be empty");
        }
    
        
        oldUser.setName(user.getName());
        oldUser.setBio(user.getBio());
        oldUser.setSpecialty(user.getSpecialty());

        return userRepo.save(oldUser);
    }
}
