package com.taximate.entities;

public class Car {

    private String id;
    private String model;
    private int numberOfPassengers;

    public Car() {

    }

    public Car(String model, int numberOfPassengers) {
        this.model = model;
        this.numberOfPassengers = numberOfPassengers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }
}
