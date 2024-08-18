package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunnerService implements ApplicationRunner {

    private final TestRunnerService service;

    @Override
    public void run(final ApplicationArguments args) {
        service.run();
    }
}
