package ru.otus.hw.service.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.security.LoginContext;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "App Shell Commands")
@RequiredArgsConstructor
public class ShellCommandsServiceImpl implements ShellCommandsService {
    private final LoginContext loginContext;

    private final LocalizedIOService ioService;

    private final TestRunnerService testRunnerService;

    @Override
    @ShellMethod(value = "Enter the name of the student being tested", key = {"l", "login"})
    public String login(@ShellOption(defaultValue = "Unknown") final String firstName,
                        @ShellOption(defaultValue = "Unknown") final String lastName) {
        loginContext.login(firstName, lastName);
        return ioService.getMessage("TestCommands.login.successful", firstName, lastName);
    }

    @Override
    @ShellMethod(value = "The beginning of the student's testing", key = {"t", "test"})
    @ShellMethodAvailability(value = "isShellTestAvailable")
    public void test() {
        testRunnerService.run();
    }

    private Availability isShellTestAvailable() {
        return loginContext.isUserLoggedIn()
                ? Availability.available()
                : Availability.unavailable(ioService.getMessage("TestCommands.test.unavailable"));
    }
}