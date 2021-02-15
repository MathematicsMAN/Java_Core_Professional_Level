package ru.GeekBrains.Lesson5HomeWork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Main {
    public static final int CARS_COUNT = 4;
    public static Semaphore smpTunnelWidth = new Semaphore(CARS_COUNT / 2);
    public static CountDownLatch cdlAllToReady;
    public static CountDownLatch cdlAllIsReady;
    public static CountDownLatch cdlAllIsStart;
    public static CountDownLatch cdlAllIsFinish;
    public volatile static boolean isWin = false;
    public volatile static boolean startRace = false;
    public volatile static boolean finishRace = false;
    public volatile static Object lockWinCar = new Object();

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        cdlAllToReady = new CountDownLatch(CARS_COUNT);
        cdlAllIsReady = new CountDownLatch(CARS_COUNT);
        cdlAllIsStart = new CountDownLatch(CARS_COUNT);
        cdlAllIsFinish = new CountDownLatch(CARS_COUNT);
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        while(!startRace){
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        while(!finishRace){
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");

    }

    public static class Car implements Runnable {
        private static int CARS_COUNT;

        static {
            CARS_COUNT = 0;
        }

        private Race race;
        private int speed;
        private String name;

        public String getName() {
            return name;
        }

        public int getSpeed() {
            return speed;
        }

        public Car(Race race, int speed) {
            this.race = race;
            this.speed = speed;
            CARS_COUNT++;
            this.name = "Участник #" + CARS_COUNT;
        }

        @Override
        public void run() {
            try {
                System.out.println(this.name + " готовится");
                cdlAllToReady.countDown();
                cdlAllToReady.await();
                Thread.sleep(500 + (int) (Math.random() * 800));
                System.out.println(this.name + " готов");
                cdlAllIsReady.countDown();
                cdlAllIsReady.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                cdlAllIsStart.countDown();
                cdlAllIsStart.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                startRace = true;
            }

            for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
            }
            synchronized (lockWinCar) {
                if (!isWin) {
                    System.out.println(this.name + " - WIN!!! @-/--- ");
                    isWin = true;
                }
            }
            try {
                cdlAllIsFinish.countDown();
                cdlAllIsFinish.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finishRace = true;
            }
        }
    }

    public abstract static class Stage {
        protected int length;
        protected String description;

        public String getDescription() {
            return description;
        }

        public abstract void go(Car c);
    }

    public static class Road extends Stage {
        public Road(int length) {
            this.length = length;
            this.description = "Дорога " + length + " метров";
        }

        @Override
        public void go(Car c) {
            try {
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
                System.out.println(c.getName() + " закончил этап: " + description);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Tunnel extends Stage {
        public Tunnel() {
            this.length = 80;
            this.description = "Тоннель " + length + " метров";
        }

        @Override
        public void go(Car c) {
            try {
                try {
                    System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                    smpTunnelWidth.acquire();
                    System.out.println(c.getName() + " начал этап: " + description);
                    Thread.sleep(length / c.getSpeed() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(c.getName() + " закончил этап: " + description);
                    smpTunnelWidth.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Race {
        private ArrayList<Stage> stages;

        public ArrayList<Stage> getStages() {
            return stages;
        }

        public Race(Stage... stages) {
            this.stages = new ArrayList<>(Arrays.asList(stages));
        }
    }
}