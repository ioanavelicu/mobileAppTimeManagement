package com.example.aplicatie.Notita;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notita implements Serializable {
    private String denumireNotita;
    private String continut;
    private boolean isPartajata;
    private String proprietar;
    private String ultimaModificare;
    private Date data;
    private List<String> utilizatoriPartajare = new ArrayList<>();

    public Notita() {
    }

    public Notita(String denumireNotita, String continut, boolean isPartajata, List<String> utilizatoriPartajare, String proprietar, String ultimaModificare, Date data) {
        this.denumireNotita = denumireNotita;
        this.continut = continut;
        this.isPartajata = isPartajata;
        this.utilizatoriPartajare = utilizatoriPartajare;
        this.proprietar = proprietar;
        this.ultimaModificare = ultimaModificare;
        this.data = data;
    }

    public String getUltimaModificare() {
        return ultimaModificare;
    }

    public void setUltimaModificare(String ultimaModificare) {
        this.ultimaModificare = ultimaModificare;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<String> getUtilizatoriPartajare() {
        return utilizatoriPartajare;
    }

    public void setUtilizatoriPartajare(List<String> utilizatoriPartajareAlesi) {
        if(utilizatoriPartajareAlesi != null) {
            for(String u: utilizatoriPartajareAlesi) {
                this.utilizatoriPartajare.add(u);
            }
        }
    }

    public String getProprietar() {
        return proprietar;
    }

    public void setProprietar(String proprietar) {
        this.proprietar = proprietar;
    }

    public boolean isPartajata() {
        return isPartajata;
    }

    public void setPartajata(boolean partajata) {
        isPartajata = partajata;
    }

    public String getDenumireNotita() {
        return denumireNotita;
    }

    public void setDenumireNotita(String denumireNotita) {
        this.denumireNotita = denumireNotita;
    }

    public String getContinut() {
        return continut;
    }

    public void setContinut(String continut) {
        this.continut = continut;
    }

    @Override
    public String toString() {
        return "Notita{" +
                "denumireNotita='" + denumireNotita + '\'' +
                ", continut='" + continut + '\'' +
                '}';
    }
}
