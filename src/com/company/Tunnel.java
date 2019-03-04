package com.company;

public class Tunnel extends Stage {

    public Tunnel(boolean isFinalStage) {
        this.length = 80;
        this.isFinalStage = isFinalStage;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            try {
                synchronized (lock) {
                    System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRace(Race race) {
        this.race = race;
    }
}
