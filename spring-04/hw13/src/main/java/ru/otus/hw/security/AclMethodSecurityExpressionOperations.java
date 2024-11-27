package ru.otus.hw.security;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

public interface AclMethodSecurityExpressionOperations extends MethodSecurityExpressionOperations {

    boolean isAdministrator(final Object targetId, Class<?> targetClass);

    boolean isAdministrator(final Object target);

    boolean canRead(final Object targetId, Class<?> targetClass);
}
