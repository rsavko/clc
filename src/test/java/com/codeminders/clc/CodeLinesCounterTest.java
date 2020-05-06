package com.codeminders.clc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeLinesCounterTest {

    @Test
    @DisplayName("Check empty string")
    void countLines_expect_0() throws IOException {
        int actualLinesCount = CodeLinesCounter.countLines(new StringReader(""));
        assertEquals(0, actualLinesCount);
    }

    @Test
    @DisplayName("File without comments with 1 line of code")
    void countLines_expect_1() throws IOException {
        int actualLinesCount = CodeLinesCounter.countLines(new FileReader("src/test/resources/OneLine.java"));
        assertEquals(1, actualLinesCount);
    }

    @Test
    @DisplayName("File contains 3 lines of code")
    void countLines_expect_3() throws IOException {
        int actualLinesCount = CodeLinesCounter.countLines(new FileReader("src/test/resources/Dave.java"));
        assertEquals(3, actualLinesCount);
    }

    @Test
    @DisplayName("File contains 5 lines of code")
    void countLines_expect_5() throws IOException {
        int actualLinesCount = CodeLinesCounter.countLines(new FileReader("src/test/resources/Hello.java"));
        assertEquals(5, actualLinesCount);
    }
}