package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements ApplicationRunner {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @Override
    public void run(final ApplicationArguments args) {
        final Student student = studentService.determineCurrentStudent();
        final TestResult testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
    }
}
