package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.configuration.SecurityConfig;
import ru.otus.hw.entity.User;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.UserDetailsServiceImpl;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(controllers = LoginController.class)
@Import({SecurityConfig.class, UserDetailsServiceImpl.class})
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void failPage() throws Exception {
        final Optional<User> userOptional = Optional.of(new User());
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(userOptional);
        mockMvc.perform(post("/login-fail"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPages/login-fail"));
    }

    @Test
    void forbiddenPage() throws Exception {
        final Optional<User> userOptional = Optional.of(new User());
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(userOptional);
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPages/forbidden"));
    }
}
