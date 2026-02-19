package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    public List<User> searchUsersBySimilarName(String q, int page, int size) {
        return userRepo.searchUsersBySimilarName(q, PageRequest.of(page, size)).getContent(); // si es Slice/Page
    }
}
