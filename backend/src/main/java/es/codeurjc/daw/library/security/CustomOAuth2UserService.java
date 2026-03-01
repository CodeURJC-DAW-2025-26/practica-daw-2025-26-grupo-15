package es.codeurjc.daw.library.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Autowired
    private OAuthUserService oauthUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        String providerId;
        String email;
        String name;
        String photo;
        String nameAttributeKey;
        
        if ("github".equals(provider)) {
            Integer id = oauth2User.getAttribute("id");
            providerId = id != null ? id.toString() : null;
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            photo = oauth2User.getAttribute("avatar_url");
            nameAttributeKey = "id";
        } else {
            providerId = oauth2User.getAttribute("sub");
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            photo = oauth2User.getAttribute("picture");
            nameAttributeKey = "sub";
        }

        List<GrantedAuthority> authorities = oauthUserService.processOAuthUser(
                provider, providerId, email, name, photo);

        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), nameAttributeKey);
    }
}
    