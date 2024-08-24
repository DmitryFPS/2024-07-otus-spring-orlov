package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;
import ru.otus.hw.security.LoginContext;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final LoginContext loginContext;

    @Override
    public Student determineCurrentStudent() {
        return new Student(loginContext.getFirstName(), loginContext.getLastName());
    }
}
