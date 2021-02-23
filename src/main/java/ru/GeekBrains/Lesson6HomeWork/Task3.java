package ru.GeekBrains.Lesson6HomeWork;

public class Task3 {

    public static boolean checkArray(int[] input){
        boolean check1 = false;
        boolean check4 = false;
        for (int i : input) {
            if (i == 1){
                check1 = true;
            } else if (i == 4){
                check4 = true;
            } else {
                return false;
            }
        }
        return check1 && check4;
    }
}
