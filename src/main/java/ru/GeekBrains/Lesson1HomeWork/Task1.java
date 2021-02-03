package ru.GeekBrains.Lesson1HomeWork;

import java.util.Random;

//Написать метод, который меняет два элемента массива
// местами (массив может быть любого ссылочного типа);
public class Task1 {
    public static void main(String[] args) {
        Rotate<Integer> mas0 = new Rotate<>(1, 2, 3, 4, 5);
        Rotate<String> mas1 = new Rotate<>("a", "b", "c", "d", "e");
        Rotate<Double> mas2 = new Rotate<>(10.0, 20.0, 30.0, 40.0, 50.0);

        Rotate[] arrays = {mas0, mas1, mas2};

        for (int i = 0; i < arrays.length; i++) {
            int j1 = new Random().nextInt(arrays[i].getLength());
            int j2;
            do {
                j2 = new Random().nextInt(arrays[i].getLength());
            } while (j1 == j2);
            
            System.out.print("mas" + i + " before change: ");
            arrays[i].printArr();
            System.out.println("j1 = " + j1 + "; j2 = " + j2);

            arrays[i].change(j1,j2);

            System.out.print("mas" + i + " after  change: ");
            arrays[i].printArr();
        }
    }
}

class Rotate<T>{
    T[] obj;

    public Rotate(T... obj) {
        this.obj = obj;
    }

    public void change(int i, int j) {
        if (i < 0 || j < 0 || i>= obj.length || j >= obj.length){
            System.out.printf("Неверное значение индексов: индексы должы быть между 0 и %d",obj.length);
            return;
        }
        T temp = obj[i];
        obj[i] = obj[j];
        obj[j] = temp;
    }

    public void printArr(){
        for (T t : obj) {
            System.out.print(t.toString() + " ");
        }
        System.out.println();
    }

    public int getLength(){
        return obj.length;
    }
}
