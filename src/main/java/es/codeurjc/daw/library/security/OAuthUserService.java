package es.codeurjc.daw.library.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Service
public class OAuthUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<GrantedAuthority> processOAuthUser(String provider, String providerId,
                                                    String email, String name, String photo) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setName(name);
            user.setEmail(email);
            if (photo != null) {
                user.setPhoto(null);
            }
            userRepository.save(user);
        } else {
            Optional<User> userByEmail = userRepository.findByEmail(email);
            if (userByEmail.isPresent()) {
                user = userByEmail.get();
                user.setProvider(provider);
                user.setProviderId(providerId);
                
                userRepository.save(user);
            } else {
                user = new User(
                    name,
                    email,
                    passwordEncoder.encode(""),
                    List.of("USER"),
                    "",
                    "",
                    null,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
                );
                user.setProvider(provider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}
