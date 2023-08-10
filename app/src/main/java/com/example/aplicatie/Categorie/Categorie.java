package com.example.aplicatie.Categorie;

import java.io.Serializable;

public class Categorie implements Serializable {
    private String denumire;

    public Categorie() {
    }

    public Categorie(String denumire) {
        this.denumire = denumire;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "denumire='" + denumire + '\'' +
                '}';
    }
}
