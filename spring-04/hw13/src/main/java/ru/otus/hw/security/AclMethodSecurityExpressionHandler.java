package ru.otus.hw.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class AclMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {


    /**
     * Создает корневой объект для выражений безопасности методов, используя
     * предоставленную аутентификацию и вызов метода.
     * Этот метод переопределяет метод из базового класса для использования кастомного корневого объекта
     * {@link AclMethodSecurityExpressionRoot}.
     *
     * @param authentication Объект аутентификации, содержащий информацию об авторизованном пользователе.
     * @param invocation     Информация о вызове метода, для которого выполняется проверка безопасности.
     * @return Корневой объект {@link AclMethodSecurityExpressionRoot} для оценки выражений безопасности методов.
     */
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(final Authentication authentication,
                                                                              final MethodInvocation invocation) {

        final AclMethodSecurityExpressionRoot root = new AclMethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.getTrustResolver());
        root.setRoleHierarchy(this.getRoleHierarchy());
        root.setDefaultRolePrefix(this.getDefaultRolePrefix());

        return root;
    }
}
