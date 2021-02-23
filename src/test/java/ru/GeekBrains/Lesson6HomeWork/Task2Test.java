package ru.GeekBrains.Lesson6HomeWork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class Task2Test {
    public static Task2 task2;

    @BeforeAll
    static void init(){
        task2 = new Task2();
    }

    @CsvSource({
        "'4',''",
        "'1 2 4 4 2 3 4 1 7','1 7'",
        "'4 2 3 1 4',''",
        "'1 2 4 4 2 3 4 1','1'",
        "'4 1 2 2 3 1 7','1 2 2 3 1 7'"
    })
    @ParameterizedTest
    void subArray(String in, String res) {
        int[] inputArray = convertStringToIntArray(in);
        int[] result = convertStringToIntArray(res);
        Assertions.assertArrayEquals(result,task2.subArray(inputArray));
    }

    @Test
    void subArrayThrow(){
        int[] inputArray = {1, 2, 2, 3, 1, 7};
        int[] result = {};
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            Assertions.assertArrayEquals(result,task2.subArray(inputArray));
        });
        assertNotNull(thrown.getMessage());
    }

    private static int[] convertStringToIntArray(String inputArrayStr) {
        if (inputArrayStr.isEmpty()){
            return new int[0];
        }
        String[] inputByElem = inputArrayStr.split("\\s");
        int[] resultArray = new int[inputByElem.length];
        int i = 0;
        for (String s : inputByElem) {
            resultArray[i++] = Integer.parseInt(s);
        }
        return resultArray;
    }
}