package ru.otus.hw.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.otus.hw.security.AclMethodSecurityExpressionHandler;

import javax.sql.DataSource;

@EnableCaching
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class AclConfig {

    /**
     * источник данных, используемый для взаимодействия с базой данных
     */
    private final DataSource dataSource;

    /**
     * менеджер кэша, используемый для кэширования данных. Предоставляет доступ к различным кэшам
     */
    private final CacheManager cacheManager;


    /**
     * Создает и возвращает кэш ACL, использующий Spring Cache
     * <p>
     * - applicationAclCache это мое кастомное название хранилища кэша
     * </p>
     *
     * @return Экземпляр кэша ACL на основе Spring Cache
     */
    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(
                cacheManager.getCache("applicationAclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    /**
     * Создает и возвращает стратегию предоставления разрешений, использующую стандартную реализацию
     * и логирование в консоль
     * Этот bean отвечает за определение, имеет ли пользователь доступ к определенному ресурсу
     *
     * @return Стратегия предоставления разрешений, основанная на {@link DefaultPermissionGrantingStrategy}
     * и использующая {@link ConsoleAuditLogger} для аудита.
     */
    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    /**
     * Создает и возвращает стратегию авторизации ACL, которая разрешает доступ только пользователям с ролью "ROLE_ADMIN"
     * Или разрешает все тем пользователям которые имеют данную роль
     *
     * @return Стратегия авторизации ACL, реализованная классом {@link AclAuthorizationStrategyImpl} и
     * разрешающая доступ только пользователям с ролью "ROLE_ADMIN"
     */
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Создает и настраивает обработчик выражений безопасности методов,
     * используя оценщик разрешений ACL и оптимизатор кэша
     *
     * @return Настроенный экземпляр {@link AclMethodSecurityExpressionHandler}, использующий
     * {@link AclPermissionEvaluator} для оценки разрешений и {@link AclPermissionCacheOptimizer}
     * для оптимизации кэша разрешений
     */
    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        final AclMethodSecurityExpressionHandler expressionHandler = new AclMethodSecurityExpressionHandler();
        final AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }

    /**
     * Создает и возвращает стратегию поиска ACL, используя источник данных, кэш ACL,
     * стратегию авторизации и логирование в консоль.
     * <p>
     * • dataSource: Источник данных. Необходим для извлечения информации об ACL.
     * • aclCache(): Кэш ACL, используемый для ускорения доступа к данным ACL. Если данные уже в кэше, то
     * обращение к базе данных не требуется.
     * • aclAuthorizationStrategy(): Стратегия авторизации, которая используется для проверки прав доступа.
     * • new ConsoleAuditLogger(): Объект для логирования событий аудита в консоль.
     * </p>
     *
     * @return Стратегия поиска ACL, реализованная классом {@link BasicLookupStrategy}.
     */
    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }

    /**
     * Создает и возвращает сервис для управления ACL (списками контроля доступа),
     * использующий JDBC для взаимодействия с базой данных.
     * • dataSource: Источник данных, необходимый для выполнения SQL запросов.
     * • lookupStrategy(): Стратегия поиска ACL. Определяет,
     * как сервис будет получать информацию об ACL из базы данных (с учетом использования кэша).
     * • aclCache(): Кэш ACL. Используется для повышения производительности, кэшируя часто используемые данные ACL.
     *
     * @return Сервис для управления ACL, реализованный классом {@link JdbcMutableAclService}.
     */
    @Bean
    public JdbcMutableAclService aclService() {
        return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
    }
}
