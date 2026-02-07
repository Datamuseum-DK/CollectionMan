package dk.datamuseum.mobilereg;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configure security.
 * What can be accessed without authentication, what needs authentication.
 * The security relies heavily on @PreAuthorize.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//@Profile("!test")
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    /**
     * Constructor.
     */
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Set up protection of paths using forms-based login.
     *
     * @param http - security configuration for HTTP request.
     */
    @Bean
    @Profile("!oauth")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/css/**", "/js/**", "/favicon.svg",
                    "/login", "/about").permitAll()
            .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                    .permitAll()
            )
            .logout((logout) -> logout.permitAll())
            .httpBasic(Customizer.withDefaults())
            ;
        return http.build();
    }

    /**
     * Set up protection of paths using Single Sign-On.
     *
     * @param http - security configuration for HTTP request.
     */
    @Bean
    @Profile("oauth")
    public SecurityFilterChain filterChainOAUTH(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/css/**", "/js/**", "/favicon.svg", "/login").permitAll()
            .requestMatchers("/userprofile").authenticated()
            .anyRequest().hasAuthority("ROLE_VIEWER")
            )
            .oauth2Login((oauth2Login) -> oauth2Login
                .userInfoEndpoint((userInfo) -> userInfo
                    .userAuthoritiesMapper(grantedAuthoritiesMapper())
                )
            );
        return http.build();
    }

    /**
     * Add login method as an authority for tracing.
     * Can be used to block change of password, as this should happen at the Identity Provider.
     */
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach((authority) -> {
                GrantedAuthority mappedAuthority;

                if (authority instanceof OidcUserAuthority) {
                    OidcUserAuthority userAuthority = (OidcUserAuthority) authority;
                    mappedAuthority = new OidcUserAuthority(
                            "OIDC_USER", userAuthority.getIdToken(), userAuthority.getUserInfo());
                } else if (authority instanceof OAuth2UserAuthority) {
                    OAuth2UserAuthority userAuthority = (OAuth2UserAuthority) authority;
                    log.info("Username: {}", userAuthority.getUserNameAttributeName());
                    mappedAuthority = new OAuth2UserAuthority(
                            "OAUTH2_USER", userAuthority.getAttributes());
                } else {
                    log.info("Authority: {}", authority.toString());
                    mappedAuthority = authority;
                }

                mappedAuthorities.add(mappedAuthority);
            });

            return mappedAuthorities;
        };
    }
}
