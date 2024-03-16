package com.taximate.entities;

public class Driver {

    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String license;
    private Car car;
    private boolean isAvailable;

    public Driver() {

    }

    public Driver(String name, String email, String password, String phone, String license, Car car, boolean isAvailable) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.license = license;
        this.car = car;
        this.isAvailable = isAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
