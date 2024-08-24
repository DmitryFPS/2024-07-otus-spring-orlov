package ru.otus.hw.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InMemoryLoginContextTest {

    @Autowired
    private InMemoryLoginContext loginContext;

    @Test
    void testSetFirstNameAndLastName() {
        loginContext.login("Dima", "Orlov");
        assertThat(loginContext.getFirstName()).isEqualTo("Dima");
        assertThat(loginContext.getLastName()).isEqualTo("Orlov");
    }

    @Test
    void testReturnTrueWhenUserIsLoggedIn() {
        loginContext.login("Dima", "Orlov");
        final boolean isLoggedIn = loginContext.isUserLoggedIn();
        assertThat(isLoggedIn).isTrue();
    }

    @Test
    void testReturnFalseWhenUserIsNotLoggedIn() {
        final boolean isLoggedIn = loginContext.isUserLoggedIn();
        assertThat(isLoggedIn).isFalse();
    }
}