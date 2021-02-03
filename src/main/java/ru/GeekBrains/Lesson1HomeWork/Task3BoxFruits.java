package ru.GeekBrains.Lesson1HomeWork;

import java.util.Random;

public class Task3BoxFruits {
    public static void main(String[] args) {
        final int COUNT_FRUITS = 100;
        final int COUNT_BOXES_TO_FILL = 3;

        Box<Apple> appleBox1 = new Box<>();
        Box<Apple> appleBox2 = new Box<>();
        Box<Orange> orangeBox1 = new Box<>();
        Box<Orange> orangeBox2 = new Box<>();
        Box[] boxes = {appleBox1, appleBox2, orangeBox1, orangeBox2};

        for (int i = 0; i < COUNT_FRUITS; i++) {
            switch (new Random().nextInt(COUNT_BOXES_TO_FILL)) {
                case 0 -> appleBox1.addFruit(new Apple());
                case 1 -> orangeBox1.addFruit(new Orange());
                case 2 -> orangeBox2.addFruit(new Orange());
            }
        }

        System.out.println("Содержимое коробок:");
        for (Box box : boxes) {
            System.out.println(box + "; weight = " + box.getWeight());
        }

        System.out.println("Compare boxes:");
        for (int i = 0; i < boxes.length - 1; i++) {
            for (int j = i + 1; j < boxes.length; j++) {
                if (boxes[i].compare(boxes[j])){
                    System.out.printf("Weight of box#%d == Weight of box#%d\n",i,j);
                }else {
                    System.out.printf("Weight of box#%d != Weight of box#%d\n",i,j);
                }
            }
        }

        System.out.println("Пересыпаем яблоки из 1-й коробки с яблоками во вторую.");
        appleBox1.pourToAnotherBox(appleBox2);
        System.out.println("Пересыпаем апельсины из 1-й коробки с апельсинами во вторую.");
        orangeBox1.pourToAnotherBox(orangeBox2);

        System.out.println("Содержимое коробок:");
        for (Box box : boxes) {
            System.out.println(box + "; weight = " + box.getWeight());
        }

    }
}
