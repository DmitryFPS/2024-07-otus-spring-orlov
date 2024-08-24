package ru.otus.hw.service.shell;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.security.LoginContext;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestRunnerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ShellCommandsServiceImplTest {
    @MockBean
    private LoginContext loginContext;

    @MockBean
    private LocalizedIOService ioService;

    @MockBean
    private TestRunnerService testRunnerService;

    @Autowired
    private ShellCommandsServiceImpl shellCommandsService;

    @Test
    public void testLogin() {
        final String firstName = "Dima";
        final String lastName = "Orlov";
        when(ioService.getMessage(anyString(), anyString(), anyString())).thenReturn("Login successful.");
        final String result = shellCommandsService.login(firstName, lastName);
        assertEquals("Login successful.", result);
    }

    @Test
    public void testRunnerService() {
        when(loginContext.isUserLoggedIn()).thenReturn(true);
        shellCommandsService.test();

        verify(testRunnerService).run();
        Mockito.verifyNoMoreInteractions(loginContext);
        Mockito.verifyNoMoreInteractions(ioService);
        Mockito.verifyNoMoreInteractions(testRunnerService);
    }
}