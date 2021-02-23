package ru.GeekBrains.Lesson6HomeWork;

import java.util.Arrays;

public class Task2 {
    private final static int NUMBER_IN_ARRAY = 4;

    public int[] subArray(int[] input){
        int lastPositionOf = -1;
        for (int i = input.length - 1; i >= 0; i--) {
            if (input[i] == NUMBER_IN_ARRAY){
                lastPositionOf = i;
                break;
            }
        }
        if (lastPositionOf == -1){
            throw new RuntimeException("В массиве отсутствует ключевой элемент");
        }
        int[] result = {};
        if (lastPositionOf != input.length - 1){
            result = Arrays.copyOfRange(input,lastPositionOf + 1,input.length);
        }
        return result;
    }
}
