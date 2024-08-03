package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TestRunnerServiceImplTest {
    @Test
    void testRun() {
        final TestService mockTestService = Mockito.mock(TestService.class);
        final TestRunnerServiceImpl testRunnerService = new TestRunnerServiceImpl(mockTestService);
        testRunnerService.run();
        Mockito.verify(mockTestService).executeTest();
    }

}