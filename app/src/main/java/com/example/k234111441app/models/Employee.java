package com.example.k234111441app.models;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Employee implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String placeOfBirth;
    private Department department;

    public Employee() {
    }

    public Employee(String id, String name, String phone, String placeOfBirth, Department department) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.placeOfBirth = placeOfBirth;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @NonNull
    @Override
    public String toString() {
        return id + " - " + name + " - " + phone + (department != null ? " (" + department.getName() + ")" : "");
    }
}
