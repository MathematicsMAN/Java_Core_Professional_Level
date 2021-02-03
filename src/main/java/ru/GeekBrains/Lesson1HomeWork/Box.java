package ru.GeekBrains.Lesson1HomeWork;

import java.util.ArrayList;
import java.util.List;

public class Box <T extends Fruit > {
    private List<T> fruits;

    public Box() {
        this.fruits = new ArrayList<>();
    }

    public float getWeight(){
        if (fruits == null || fruits.size() == 0) return 0;
        return fruits.size() * fruits.get(0).getWeight();
    }

    public boolean compare(Box o) {
        if (this == o) return true;
        return this.getWeight() == o.getWeight();
    }

    public void pourToAnotherBox(Box<T> o) {
        if (this == o) return;
        o.addListFruits(fruits);
        fruits.clear();
    }

    public void addFruit(T fruit){
        if(fruits == null ) return;
        fruits.add(fruit);
    }

    private void addListFruits(List<T> fruitsList){
        if(fruitsList == null || fruitsList.size() == 0) {
            return;
        }
        fruits.addAll(fruitsList);
    }

    @Override
    public String toString() {
        String str = "N = " + fruits.size();
        str += fruits.size() == 0 ? "" : "; Box of " + fruits.get(0).toString() + "s";
        return str;
    }
}
