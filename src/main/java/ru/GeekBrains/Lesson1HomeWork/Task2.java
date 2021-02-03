package ru.GeekBrains.Lesson1HomeWork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Написать метод, который преобразует массив в ArrayList
public class Task2 {
    public static void main(String[] args) {
        List<Integer> resultIntList = new ArrayList<>();
        List<String> resultStrList = new ArrayList<>();
        List<Double> resultDoubleList = new ArrayList<>();

        Integer[] masInt = {10, 11, 12, 13, 14, 15, 16, 17};
        String[] masStr = {"zero", "one", "two", "three", "four", "five", "six", "seven"};
        Double[] masDouble = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};

        JustDoIt<Integer> doIt1 = new JustDoIt<>(masInt);
        JustDoIt<String> doIt2 = new JustDoIt<>(masStr);
        JustDoIt<Double> doIt3 = new JustDoIt<>(masDouble);

        resultIntList = doIt1.doToList();
        resultStrList = doIt2.doToList();
        resultDoubleList = doIt3.doToList();

        System.out.println(resultIntList);
        System.out.println(resultStrList);
        System.out.println(resultDoubleList);
    }

    private static class JustDoIt<T> {
        T[] mas;

        public JustDoIt(T[] mas) {
            this.mas = mas;
        }

        public List<T> doToList() {
            return Arrays.asList(mas);
        }
    }
}
