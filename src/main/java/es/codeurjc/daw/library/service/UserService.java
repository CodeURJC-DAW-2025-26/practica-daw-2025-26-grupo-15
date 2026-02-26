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
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public Optional<User> findByName(String name) {
        return userRepo.findByName(name);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
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

    public void requestToFollow(User requester, User target) {
        if (requester.getId().equals(target.getId())) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        if (requester.getRequestedFriends().contains(target)) {
            throw new IllegalArgumentException("Follow request already sent");
        }

        if (requester.getFollowing().contains(target)) {
            throw new IllegalArgumentException("Already following this user");
        }

        requester.getRequestedFriends().add(target);
        target.getRequestReceived().add(requester);
        userRepo.save(requester);
        userRepo.save(target);
    }

    public boolean hasRequestedToFollow(User requester, User target) {
        return requester.getRequestedFriends().contains(target);
    }

    public void acceptFollowRequest(User requested, Long fromUser){
        User requester = userRepo.findById(fromUser).orElseThrow(() -> new RuntimeException("User not found"));

         if (!requested.getRequestReceived().contains(requester)){
            throw new IllegalArgumentException("No follow request from this user");
        }

        requested.getRequestReceived().remove(requester);
        requester.getRequestedFriends().remove(requested);

        requested.getFollowers().add(requester);
        requester.getFollowing().add(requested);

        userRepo.save(requester);
        userRepo.save(requested);
    }

    public void declineFollowRequest(User requested, Long fromUser){
        User requester = userRepo.findById(fromUser).orElseThrow(() -> new RuntimeException("User not found"));

         if (!requested.getRequestReceived().contains(requester)){
            throw new IllegalArgumentException("No follow request from this user");
        }

        requested.getRequestReceived().remove(requester);
        requester.getRequestedFriends().remove(requested);

        userRepo.save(requester);
        userRepo.save(requested);
    }

    public User register(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Username cannot be null or empty.");
        }
        if (user.getName().length() < 3 || user.getName().length() > 30) {
            throw new RuntimeException("Username must be between 3 and 30 characters.");
        }
        if (user.getName().matches(".*[^a-zA-Z0-9_].*")) {
            throw new RuntimeException("Username can only contain letters, numbers and underscores.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email cannot be null or empty.");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format.");
        }
        if (user.getEncodedPassword() == null || user.getEncodedPassword().trim().isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty.");
        }
        if (user.getEncodedPassword().length() < 8 || user.getEncodedPassword().length() > 64) {
            throw new RuntimeException("Password must be between 8 and 64 characters long.");
        }
        if (user.getEncodedPassword().matches(".*\\s.*")) {
            throw new RuntimeException("Password cannot contain whitespace.");
        }
        if (!user.getEncodedPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#_\\-]).{8,64}$")) {
            throw new RuntimeException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
        if (existsByName(user.getName())) {
            throw new RuntimeException("Username '" + user.getName() + "' is already taken.");
        }

        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email '" + user.getEmail() + "' is already registered.");
        }
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));
        user.setRoles(java.util.List.of("USER"));
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
                    Image updated = imageService.replaceImageFile(oldUser.getPhoto().getId(),
                            photoFile.getInputStream());
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

    // because i want to return the suggestion to follow and the contacts in common with that suggestion
    private record UserPair(User suggestion, List<User> contact) {
    }

    public List<UserPair> getFollowingSuggestions(User user) {
        Map<Long, UserPair> suggestionsMap = new LinkedHashMap<>();
        int limit = 30;
        int limitPerContact = 5; // Limit suggestions per followed user to avoid a list of suggestions from a single contact
        if (user.getFollowing().isEmpty()) {
            return userRepo.findRandomUsers(PageRequest.of(0, limit)).stream()
                    .filter(u -> !u.getId().equals(user.getId()) && !user.getRequestedFriends().contains(u))
                    .map(u -> new UserPair(u, new ArrayList<>()))
                    .toList();
        }

        Set<Long> alreadyFollowing = user.getFollowing().stream().map(User::getId).collect(Collectors.toSet()); //To avoid duplicates in suggestions when multiple contacts follow the same user
        for (User following : user.getFollowing()) {
            if (suggestionsMap.size() >= limit)
                break;
            int count = 0;
            for (User suggestion : following.getFollowing()) {
                if (count >= limitPerContact || suggestionsMap.size() >= limit) break;

                Long sId = suggestion.getId();
                if (!sId.equals(user.getId()) && !alreadyFollowing.contains(sId) && !user.getRequestedFriends().contains(suggestion)) {
                    if (suggestionsMap.containsKey(sId)) {
                        List<User> contacts = suggestionsMap.get(sId).contact();
                        if (!contacts.contains(following)) {
                            contacts.add(following);
                        }
                    } else {
                        List<User> contacts = new ArrayList<>();
                        contacts.add(following);
                        suggestionsMap.put(sId, new UserPair(suggestion, contacts));
                        count++;
                    }
                }
            }
        }

        List<UserPair> suggestions = new ArrayList<>(suggestionsMap.values());

        if (suggestions.size() < limit) {
            List<UserPair> randomSuggestions = userRepo.findRandomUsers(PageRequest.of(0, limit - suggestions.size()))
                    .stream()
                    .filter(u -> !u.getId().equals(user.getId()) && !user.getRequestedFriends().contains(u)
                            && suggestions.stream().noneMatch(s -> s.suggestion.getId().equals(u.getId())))
                    .map(u -> new UserPair(u, new ArrayList<>()))
                    .toList();
            suggestions.addAll(randomSuggestions);
        }

        Collections.shuffle(suggestions);
        return suggestions;
    }

    public void deleteUser(User user,long id,boolean isAdmin){
        if(user.getId() != id  && !isAdmin)
            throw new RuntimeException("You don't have permission to delete this profile.");

        User deletedUser = null;
        if(user.getId() == id)
            deletedUser = user;
        else
            deletedUser = this.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            
        for(User follower : deletedUser.getFollowers()){
            follower.getFollowing().remove(deletedUser);
            userRepo.save(follower);
        }
        deletedUser.getFollowers().clear();

        for(User following: deletedUser.getFollowing()){
            following.getFollowers().remove(deletedUser);
            userRepo.save(following);
        }
        deletedUser.getFollowing().clear();

        for(User sender: deletedUser.getRequestReceived()){
            sender.getRequestedFriends().remove(deletedUser);
            userRepo.save(sender);
        }
        deletedUser.getRequestReceived().clear();

        for(User reciver: deletedUser.getRequestedFriends()){
            reciver.getRequestReceived().remove(deletedUser);
            userRepo.save(reciver);
        }
        deletedUser.getRequestedFriends().clear();

        userRepo.save(deletedUser);
        userRepo.delete(deletedUser);

    }
}
