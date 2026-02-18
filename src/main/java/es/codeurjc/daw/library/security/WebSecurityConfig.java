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
						.requestMatchers("/solution/**").permitAll()
						.requestMatchers("/newsolution/**").permitAll()
						.requestMatchers("/editsolution/**").permitAll()

						.requestMatchers("/assets/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/profile").permitAll()
						.requestMatchers("/edit-profile").permitAll()
						.requestMatchers("/follow-requests").permitAll()
						.requestMatchers("/following").permitAll()
						.requestMatchers("/admin").permitAll()

						.requestMatchers("/exercise").permitAll()
						.requestMatchers("/list-view").permitAll()
						.requestMatchers("/new-list").permitAll()
						.requestMatchers("/new-exercise").permitAll()
                        .requestMatchers("/error").permitAll()
						// PRIVATE PAGES
						.requestMatchers("/newbook").hasAnyRole("USER")
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/")
						.permitAll())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService)   // plain OAuth2 providers
								.oidcUserService(oidcUserService())      // OIDC providers (Google)
						)
						.defaultSuccessUrl("/")
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
