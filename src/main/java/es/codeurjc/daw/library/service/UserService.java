package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.User;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired 
    private UserRepository userRepo;

    public Optional<User> findByName(String name){
        return userRepo.findByName(name);
    }


    public Optional<User> findByEmail(String email){
        return userRepo.findByEmail(email);
    }
    public Optional<User> findByProviderAndProviderId(String provider, String providerId){
        return userRepo.findByProviderAndProviderId(provider, providerId);
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
