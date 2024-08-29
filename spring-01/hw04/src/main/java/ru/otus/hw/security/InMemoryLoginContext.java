package ru.otus.hw.security;

import lombok.Getter;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Getter
@Component
public class InMemoryLoginContext implements LoginContext {
    private String firstName;

    private String lastName;

    @Override
    public void login(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean isUserLoggedIn() {
        return nonNull(firstName) && nonNull(lastName);
    }
}