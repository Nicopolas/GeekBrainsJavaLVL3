package com.company;

public class Tunnel extends Stage {
    private static int CARS_IN_TUNNEL = 0;

    public Tunnel(boolean isFinalStage) {
        this.length = 80;
        this.isFinalStage = isFinalStage;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                if (CARS_IN_TUNNEL >= (race.getCARS_COUNT() / 2)) {
                    System.out.println(c.getName() + " не может заехать в: " + description);
                    while (CARS_IN_TUNNEL >= (race.getCARS_COUNT() / 2)) {
                        Thread.sleep(100);
                    }
                }

                CARS_IN_TUNNEL++;
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
                System.out.println(c.getName() + " закончил этап: " + description);
                CARS_IN_TUNNEL--;

                if (isFinalStage) {
                    if (CARS_FINISHED == 0) {
                        System.out.println(c.getName() + " -  WIN!!!");
                    }
                    synchronized (lock) {
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
