package com.company;

public abstract class Stage {
    protected int length;
    protected String description;
    protected boolean isFinalStage;
    protected Race race;
    protected static int CARS_FINISHED = 0;
    protected static final Object lock = new Object();

    public String getDescription() {
        return description;
    }

    public abstract void go(Car c);

    public abstract void setRace(Race race);
}
