package ru.otus.hw.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppPropertiesTest {
    final AppProperties properties = new AppProperties("test");

    @Test
    void testConstructor() {
        final String expectedFileName = "test";
        assertEquals(expectedFileName, properties.getTestFileName());
    }

    @Test
    void testGet() {
        final String expectedFileName = "test";
        assertEquals(expectedFileName, properties.getTestFileName());
    }

    @Test
    void testSet() {
        final String updatedFileName = "test";
        properties.setTestFileName(updatedFileName);
        assertEquals(updatedFileName, properties.getTestFileName());
    }
}
