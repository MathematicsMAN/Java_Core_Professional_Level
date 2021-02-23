package ru.GeekBrains.Lesson6HomeWork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class Task3Test {
    private static Task3 task3;

    @BeforeAll
    static void init(){
        task3 = new Task3();
    }

    @CsvSource({
    "'1 1 1 4 4 1 4',true",
    "'1 1 1 1 1 1',false",
    "'4 1 1 1 1 1 1',true",
    "'4 4 4 4',false",
    "'1 4 4 4 4',true",
    "'1 4 4 1 1 4 3',false"
    })
    @ParameterizedTest
    void checkArray(String in, String res) {
        int[] inputArray = convertStringToIntArray(in);
        boolean result = "true".equals(res);
        Assertions.assertEquals(result, Task3.checkArray(inputArray));
    }

    private int[] convertStringToIntArray(String inputArrayStr) {
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