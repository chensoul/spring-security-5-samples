package com.chensoul.security.config;

import com.chensoul.security.spring.RedisUserCache;
import com.chensoul.security.spring.user.CustomUserDetailsService;
import com.chensoul.security.spring.user.UserRepository;
import com.chensoul.security.spring.userlocation.DifferentLocationChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final UserRepository userRepository;
    private final DifferentLocationChecker differentLocationChecker;
    private final RedisTemplate redisTemplate;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth.anyRequest().authenticated())
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .authenticationProvider(daoAuthenticationProvider()); //这是增加一个
        return http.build();
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPostAuthenticationChecks(differentLocationChecker);
        daoAuthenticationProvider.setUserCache(new RedisUserCache(redisTemplate));

        //security 6.3
//        daoAuthenticationProvider.setCompromisedPasswordChecker(new ResourcePasswordChecker(
//                new ClassPathResource("10-million-password-list-top-1000000.txt")));
//        daoAuthenticationProvider.setCompromisedPasswordChecker(new HaveIBeenPwnedRestApiPasswordChecker());
        return daoAuthenticationProvider;
    }

//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider authenticationProvider = daoAuthenticationProvider();
//        ProviderManager providerManager = new ProviderManager(authenticationProvider);
//        providerManager.setEraseCredentialsAfterAuthentication(false);
//        return providerManager;
//    }

}
