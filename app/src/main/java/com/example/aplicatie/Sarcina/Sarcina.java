package com.example.aplicatie.Sarcina;

import java.io.Serializable;

public class Sarcina implements Serializable {
    private String descriereSarcina;
    private Boolean esteBifata;

    public Sarcina(String descriereSarcina) {
        this.descriereSarcina = descriereSarcina;
        this.esteBifata = false;
    }

    public Sarcina(String descriereSarcina, Boolean esteBifata) {
        this.descriereSarcina = descriereSarcina;
        this.esteBifata = esteBifata;
    }

    public Sarcina() {
    }

    public Boolean getEsteBifata() {
        return esteBifata;
    }

    public void setEsteBifata(Boolean esteBifata) {
        this.esteBifata = esteBifata;
    }

    public String getDescriereSarcina() {
        return descriereSarcina;
    }

    public void setDescriereSarcina(String descriereSarcina) {
        this.descriereSarcina = descriereSarcina;
    }

    @Override
    public String toString() {
        return "Sarcina{" +
                "descriereSarcina='" + descriereSarcina + '\'' +
                '}';
    }
}
