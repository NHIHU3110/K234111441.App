package com.example.k234111441app.models;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Department implements Serializable {
    private String id;
    private String name;

    public Department() {
    }

    public Department(String id, String name) {
        this.id = id;
        this.name = name;
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

    @NonNull
    @Override
    public String toString() {
        if ("ALL".equals(id)) return name;
        return id + " - " + name;
    }
}
