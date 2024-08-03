package ru.otus.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.TestRunnerService;

public class Application {
    private static final String STRING_CONTEXT_XML_PATH = "/spring-context.xml";

    public static void main(String[] args) {
        final ApplicationContext context = new ClassPathXmlApplicationContext(STRING_CONTEXT_XML_PATH);
        final TestRunnerService testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}
