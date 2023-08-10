package com.example.aplicatie.Statistici;

public class DataItem {
    private float valoare1;
    private float valoare2;
    private String eticheta1;
    private String eticheta2;
    private String numeGrafic;

    public DataItem(float valoare1, float valoare2, String eticheta1, String eticheta2, String numeGrafic) {
        this.valoare1 = valoare1;
        this.valoare2 = valoare2;
        this.eticheta1 = eticheta1;
        this.eticheta2 = eticheta2;
        this.numeGrafic = numeGrafic;
    }

    public float getValoare1() {
        return valoare1;
    }

    public float getValoare2() {
        return valoare2;
    }

    public String getEticheta1() {
        return eticheta1;
    }

    public void setEticheta1(String eticheta1) {
        this.eticheta1 = eticheta1;
    }

    public String getEticheta2() {
        return eticheta2;
    }

    public void setEticheta2(String eticheta2) {
        this.eticheta2 = eticheta2;
    }

    public String getNumeGrafic() {
        return numeGrafic;
    }

    public void setNumeGrafic(String numeGrafic) {
        this.numeGrafic = numeGrafic;
    }
}
