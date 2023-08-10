package com.example.aplicatie.Obiectiv;

import java.io.Serializable;

public class Obiectiv implements Serializable {
    private String descriere;
    private boolean isBifat;

    public Obiectiv() {
    }

    public Obiectiv(String descriere) {
        this.descriere = descriere;
    }

    public Obiectiv(String descriere, boolean isBifat) {
        this.descriere = descriere;
        this.isBifat = isBifat;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public boolean isBifat() {
        return isBifat;
    }

    public void setBifat(boolean bifat) {
        isBifat = bifat;
    }

    @Override
    public String toString() {
        return "Obiectiv{" +
                "descriere='" + descriere + '\'' +
                ", isBifat=" + isBifat +
                '}';
    }
}
