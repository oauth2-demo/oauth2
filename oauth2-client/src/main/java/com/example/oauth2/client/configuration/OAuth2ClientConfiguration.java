package com.example.oauth2.client.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
public class OAuth2ClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientConfiguration.class);


    /**
     * Build WebClient with OAuth2 Configuration.
     */
    @Bean
    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository, authorizedClientRepository
        );

        return WebClient.builder().apply(oauth2.oauth2Configuration()).build();
    }

    /**
     * The users here are no use, because we set permitAll() for all. The target of this project is test the WebClient bean,
     * which should be able to retrieve resources from OAuth2 Resource Server(http://127.0.0.1:8090/).
     */
    @Bean
    UserDetailsService userDetailsService(){
        UserDetails user = User.withUsername("test").password(passwordEncoder().encode("123")).roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }


    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Set permitAll() for all requests. We will only check WebClient should work as oauth2 client successfully.
     */
    @Bean
    public SecurityFilterChain defaultChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll().and().formLogin()
                .and().oauth2Client();

        return http.build();
    }
}
