package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import java.util.List;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private RepositoryUserDetailsService userDetailsService;

	@Autowired
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

	@Autowired
	private OAuthUserService oauthUserService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/form-register").permitAll()
						.requestMatchers("/assets/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers( "/images/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers( "/exercise/{id}").permitAll()
						.requestMatchers("/list-view/{id}").permitAll()
						.requestMatchers( "/solution/*").permitAll()
                        .requestMatchers("/error").permitAll()
						.requestMatchers("/searchUsers**").permitAll()
						.requestMatchers("/searchPosts**").permitAll()
						.requestMatchers("/searchLists**").permitAll()
						.requestMatchers("/loginerror").permitAll()
						.requestMatchers("/profile/{id}").permitAll()
						.requestMatchers("/followers-following/**").permitAll()
						
						// PRIVATE PAGES
						.requestMatchers("/new-list").hasAnyRole("USER")
						.requestMatchers("/new-exercise").hasAnyRole("USER")
						.requestMatchers("/add-solution/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/exercise/*/new-solution").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/exercise/*/solution/*/delete").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/edit-list/**").hasAnyRole("USER")
						.requestMatchers("/solution/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/requestToFollow").hasAnyRole("USER")
						.requestMatchers("/profile").hasAnyRole("USER", "ADMIN") 
						.requestMatchers("/acceptRequest/**").hasAnyRole("USER")
						.requestMatchers("/declineRequest/**").hasAnyRole("USER")
						.requestMatchers("/edit-profile").hasAnyRole("USER")
						.requestMatchers("/new-exercise").hasAnyRole("USER")
						.requestMatchers("/edit-exercise/**").hasAnyRole("USER")
						.requestMatchers("/newsolution/**").hasAnyRole("USER")		
						.requestMatchers("/editsolution/**").hasAnyRole("USER")
						.requestMatchers("/following").hasAnyRole("USER")
						.requestMatchers("/new-list").hasAnyRole("USER")
						.requestMatchers("/admin").hasAnyRole("ADMIN")
						.requestMatchers("/adminSearch**").hasAnyRole("ADMIN")
						.requestMatchers("/loadModals/**").hasAnyRole("ADMIN")
						.requestMatchers("/follow-requests").hasAnyRole("USER")				
						.anyRequest().authenticated())
						
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.usernameParameter("email")
						.passwordParameter("password")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)   // plain OAuth2 providers
								.oidcUserService(oidcUserService())      // OIDC providers (Google)
						)
						.defaultSuccessUrl("/", true)
						.failureUrl("/loginerror")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}


	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();
		return userRequest -> {
			OidcUser oidcUser = delegate.loadUser(userRequest);

			String provider  = userRequest.getClientRegistration().getRegistrationId();
			String providerId = oidcUser.getAttribute("sub");
			String email      = oidcUser.getAttribute("email");
			String name       = oidcUser.getAttribute("name");
			//Corregir URL Imagen

			List<GrantedAuthority> authorities = oauthUserService.processOAuthUser(
					provider, providerId, email, name, "img");

			return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
		};
	}
}
