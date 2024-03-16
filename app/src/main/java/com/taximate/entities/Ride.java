package com.taximate.entities;

public class Ride {

    private String rideId;
    private String driverId;
    private String customerId;
    private String driverName;
    private String customerName;
    private String customerLocation;
    private String customerDestination;
    private String status;
    private double cost;

    public Ride() {

    }

    public Ride(String driverId,
                String customerId,
                String driverName,
                String customerName,
                String customerLocation,
                String customerDestination,
                String status,
                double cost) {
        this.driverId = driverId;
        this.customerId = customerId;
        this.driverName = driverName;
        this.customerName = customerName;
        this.customerLocation = customerLocation;
        this.customerDestination = customerDestination;
        this.status = status;
        this.cost = cost;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(String customerLocation) {
        this.customerLocation = customerLocation;
    }

    public String getCustomerDestination() {
        return customerDestination;
    }

    public void setCustomerDestination(String customerDestination) {
        this.customerDestination = customerDestination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
