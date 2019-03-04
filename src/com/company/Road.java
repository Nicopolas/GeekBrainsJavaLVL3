package com.company;

public class Road extends Stage {

    public Road(int length, boolean isFinalStage) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
        this.isFinalStage = isFinalStage;
    }

    @Override
    public void go(Car c) {
        try {
            synchronized (lock) {
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
                System.out.println(c.getName() + " закончил этап: " + description);

                if (isFinalStage) {
                    CARS_FINISHED++;
                    while (CARS_FINISHED != race.getCARS_COUNT()) {
                        lock.wait();
                    }
                    race.setAllCarsIsFinished(true);
                    lock.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRace(Race race) {
        this.race = race;
    }
}
