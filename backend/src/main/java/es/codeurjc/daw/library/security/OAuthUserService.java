package es.codeurjc.daw.library.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.ImageService;

@Service
public class OAuthUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    public List<GrantedAuthority> processOAuthUser(String provider, String providerId,
                                                    String email, String name, String photo) {
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("OAuth providerId is missing");
        }

        String normalizedName = normalizeName(name, provider, providerId);
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setName(normalizedName);
            if (email != null && !email.isBlank()) {
                user.setEmail(email.trim());
            }
            syncOAuthAvatar(user, photo);
            userRepository.save(user);
        } else {
            Optional<User> userByEmail = Optional.empty();
            if (email != null && !email.isBlank()) {
                userByEmail = userRepository.findByEmail(email.trim());
            }

            if (userByEmail.isPresent()) {
                user = userByEmail.get();
                user.setName(normalizedName);
                user.setProvider(provider);
                user.setProviderId(providerId);
                syncOAuthAvatar(user, photo);
                userRepository.save(user);
            } else {
                String normalizedEmail = normalizeEmail(email, provider, providerId);
                user = new User(
                    normalizedName,
                    normalizedEmail,
                    passwordEncoder.encode(""),
                    List.of("USER"),
                    "",
                    "",
                    null,
                    new ArrayList<>()
                );
                user.setProvider(provider);
                user.setProviderId(providerId);
                syncOAuthAvatar(user, photo);
                userRepository.save(user);
            }
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }

    private String normalizeName(String name, String provider, String providerId) {
        if (name == null || name.isBlank()) {
            return provider + "_user_" + providerId;
        }
        return name.trim();
    }

    private String normalizeEmail(String email, String provider, String providerId) {
        if (email == null || email.isBlank()) {
            return provider + "_" + providerId + "@oauth.local";
        }
        return email.trim();
    }

    private void syncOAuthAvatar(User user, String photoUrl) {
        if (photoUrl == null || photoUrl.isBlank()) {
            return;
        }
        try (InputStream imageStream = openRemoteImage(photoUrl)) {
            if (user.getPhoto() != null) {
                imageService.replaceImageFile(user.getPhoto().getId(), imageStream);
            } else {
                user.setPhoto(imageService.createImage(imageStream));
            }
        } catch (Exception e) {
            System.err.println("[OAuthUserService] Could not sync OAuth avatar: " + e.getMessage());
        }
    }

    private InputStream openRemoteImage(String photoUrl) throws IOException {
        URL imageUrl = URI.create(photoUrl).toURL();
        URLConnection connection = imageUrl.openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(7000);
        return connection.getInputStream();
    }
}
