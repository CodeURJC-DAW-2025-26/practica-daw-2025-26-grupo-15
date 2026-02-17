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
}
