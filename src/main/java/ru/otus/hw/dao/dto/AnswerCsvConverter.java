package ru.otus.hw.dao.dto;

import com.opencsv.bean.AbstractCsvConverter;
import ru.otus.hw.domain.Answer;

import static ru.otus.hw.common.Constants.PERCENT;

public class AnswerCsvConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(final String value) {
        final String[] valueArr = value.split(PERCENT);
        return new Answer(valueArr[0], Boolean.parseBoolean(valueArr[1]));
    }
}
