package com.company;

public class Car implements Runnable {
    private static final Object lock = new Object();
    private static int CARS_READY = 0;
    private static int CARS_COUNT = 0;

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
            synchronized (lock) {
                System.out.println(this.name + " готовится");
                Thread.sleep(500 + (int) (Math.random() * 800));
                System.out.println(this.name + " готов");
                CARS_READY++;
                while (CARS_READY != CARS_COUNT) {
                    lock.wait();
                }
                race.setAllCarsIsReady(true);
                lock.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
    }
}