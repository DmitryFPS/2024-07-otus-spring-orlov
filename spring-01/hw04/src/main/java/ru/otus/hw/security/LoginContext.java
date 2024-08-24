package ru.otus.hw.security;

public interface LoginContext {
    void login(final String firstName, final String lastName);

    boolean isUserLoggedIn();

    String getFirstName();

    String getLastName();
}