package ru.otus.hw.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

/**
 * Расширенный корневой объект для выражений безопасности методов Spring Security,
 * добавляющий поддержку ACL
 * Расширяет {@link SecurityExpressionRoot} и реализует {@link AclMethodSecurityExpressionOperations}
 * Предоставляет методы для проверки разрешений на основе ACL
 */
public class AclMethodSecurityExpressionRoot extends SecurityExpressionRoot
        implements AclMethodSecurityExpressionOperations {

    /**
     * Фильтруемый объект
     */
    private Object filterObject;

    /**
     * Возвращаемый объект
     */
    private Object returnObject;

    /**
     * Целевой объект
     */
    private Object target;


    /**
     * Конструктор, инициализирующий объект аутентификации.
     *
     * @param authentication Объект аутентификации.
     */
    public AclMethodSecurityExpressionRoot(final Authentication authentication) {
        super(authentication);
    }


    /**
     * Устанавливает целевой объект.
     *
     * @param target Целевой объект.
     */
    void setThis(final Object target) {
        this.target = target;
    }


    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setFilterObject(final Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public void setReturnObject(final Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    /**
     * Проверяет, является ли пользователь администратором для указанного объекта.
     *
     * @param targetId    Идентификатор целевого объекта.
     * @param targetClass Класс целевого объекта.
     * @return true, если пользователь является администратором, иначе false.
     */
    @Override
    public boolean isAdministrator(final Object targetId, Class<?> targetClass) {
        return isGranted(targetId, targetClass, admin);
    }

    /**
     * Проверяет, является ли пользователь администратором для указанного объекта.
     *
     * @param target Целевой объект.
     * @return true, если пользователь является администратором, иначе false.
     */
    @Override
    public boolean isAdministrator(final Object target) {
        return hasPermission(target, admin);
    }

    /**
     * Проверяет, имеет ли пользователь право на чтение указанного объекта.
     *
     * @param targetId    Идентификатор целевого объекта.
     * @param targetClass Класс целевого объекта.
     * @return true, если пользователь имеет право на чтение, иначе false.
     */
    @Override
    public boolean canRead(final Object targetId, Class<?> targetClass) {
        if (isAdministrator(targetId, targetClass)) {
            return true;
        }
        return isGranted(targetId, targetClass, read);
    }

    /**
     * Проверяет, имеет ли пользователь указанное разрешение для указанного объекта.
     *
     * @param targetId    Идентификатор целевого объекта.
     * @param targetClass Класс целевого объекта.
     * @param permission  Разрешение.
     * @return true, если пользователь имеет разрешение, иначе false.
     */
    boolean isGranted(final Object targetId, Class<?> targetClass, final Object permission) {
        return hasPermission(targetId, targetClass.getCanonicalName(), permission);
    }
}
