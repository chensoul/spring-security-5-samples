package com.chensoul.security;

import com.chensoul.security.entity.Authority;
import com.chensoul.security.entity.User;
import com.chensoul.security.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(
        exclude = {DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class})
public class MainTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Authenticating with wrong user")
    public void loggingInWithWrongUser() throws Exception {
        mvc.perform(formLogin())
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Skip authentication and test the controller method")
    @WithMockUser(username = "mary", password = "pass")
    public void skipAuthenticationTest() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, mary!")));
    }

    @Test
    @DisplayName("Test authentication with valid user")
    public void testAuthenticationWithValidUser() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("mary");
        mockUser.setPassword(passwordEncoder.encode("pass"));

        Authority a = new Authority();
        a.setName("read");
        a.setUser(mockUser);
        mockUser.setAuthorities(List.of(a));

        when(userRepository.findUserByUsername("mary"))
                .thenReturn(Optional.of(mockUser));

        mvc.perform(formLogin()
                        .user("mary")
                        .password("pass"))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Test authentication with inexistent user")
    public void testAuthenticationWithInexistentUser() throws Exception {
        when(userRepository.findUserByUsername("mary"))
                .thenReturn(Optional.empty());

        mvc.perform(formLogin()
                        .user("mary")
                        .password("pass"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Test authentication with invalid password")
    public void testAuthenticationWithInvalidPassword() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("mary");
        mockUser.setPassword(passwordEncoder.encode("pass"));

        Authority a = new Authority();
        a.setName("read");
        a.setUser(mockUser);
        mockUser.setAuthorities(List.of(a));

        when(userRepository.findUserByUsername("mary"))
                .thenReturn(Optional.of(mockUser));

        mvc.perform(formLogin()
                        .user("mary")
                        .password("pass1"))
                .andExpect(unauthenticated());
    }
}