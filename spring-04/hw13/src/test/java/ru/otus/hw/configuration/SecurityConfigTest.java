package ru.otus.hw.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;


    @Test
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void testAuthenticatedAdminAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void testAuthenticatedUserAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"));
    }

    @Test
    void testAccessPermitAllLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testAccessPermitAllLoginFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login-fail"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPages/login-fail"));
    }

    @Test
    void testAccessPermitAllLogout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testAccessPermitAllForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/forbidden"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPages/forbidden"));
    }

    @Test
    void testPasswordEncoder() {
        final PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        final String password = "password";
        final String encodedPassword = passwordEncoder.encode(password);
        assertNotEquals(password, encodedPassword);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }

    @Test
    void testSuccessfulAuthentication() {
        final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        final PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        final String password = "password";
        final String encodedPassword = passwordEncoder.encode(password);
        final UserDetails user = User.builder().username("user").password(encodedPassword).roles("USER").build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);

        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        final Authentication authentication = authProvider
                .authenticate(new UsernamePasswordAuthenticationToken("user", password));
        assertEquals("user", authentication.getName());
    }

    @Test
    void testFailedAuthentication() {
        final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        final UserDetails user = User.builder().username("user")
                .password(passwordEncoder.encode("password")).roles("USER").build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);

        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        assertThrows(BadCredentialsException.class, () ->
                authProvider.authenticate(
                        new UsernamePasswordAuthenticationToken("user", "bad-password"))
        );
    }
}
