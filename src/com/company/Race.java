package com.company;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class Race {
    private boolean allCarsIsReady = false;
    private boolean allCarsIsFinished = false;
    private ArrayList<Stage> stages;
    private Car[] cars;
    private int CARS_COUNT;

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public Race(int CARS_COUNT, Stage... stages) {
        this.CARS_COUNT = CARS_COUNT;
        this.stages = new ArrayList<>(Arrays.asList(stages));
        for (Stage stage : stages) {
            stage.setRace(this);
        }
    }

    public void start() {

        cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(this, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        while (!allCarsIsReady){
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        while (!allCarsIsFinished){
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

    public void setAllCarsIsReady(boolean allCarsIsReady) {
        this.allCarsIsReady = allCarsIsReady;
    }

    public void setAllCarsIsFinished(boolean allCarsIsFinished) {
        this.allCarsIsFinished = allCarsIsFinished;
    }

    public int getCARS_COUNT() {
        return CARS_COUNT;
    }
}
