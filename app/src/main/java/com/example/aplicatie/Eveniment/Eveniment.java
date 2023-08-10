package com.example.aplicatie.Eveniment;

import com.example.aplicatie.Reminder.Reminder;

import java.io.Serializable;
import java.util.Date;

public class Eveniment implements Serializable {
    private String denumireEveniment;
    private Date dataEveniment;
    private String descriereEveniment;
    private String categorieEveneiment;
    private Reminder reminder;

    public Eveniment() {
    }

    public Eveniment(String denumireEveniment, Date dataEveniment, String descriereEveniment, String categorieEveneiment, Reminder reminder) {
        this.denumireEveniment = denumireEveniment;
        this.dataEveniment = dataEveniment;
        this.descriereEveniment = descriereEveniment;
        this.categorieEveneiment = categorieEveneiment;
        this.reminder = reminder;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public String getDenumireEveniment() {
        return denumireEveniment;
    }

    public void setDenumireEveniment(String denumireEveniment) {
        this.denumireEveniment = denumireEveniment;
    }

    public Date getDataEveniment() {
        return dataEveniment;
    }

    public void setDataEveniment(Date dataEveniment) {
        this.dataEveniment = dataEveniment;
    }

    public String getDescriereEveniment() {
        return descriereEveniment;
    }

    public void setDescriereEveniment(String descriereEveniment) {
        this.descriereEveniment = descriereEveniment;
    }

    public String getCategorieEveneiment() {
        return categorieEveneiment;
    }

    public void setCategorieEveneiment(String categorieEveneiment) {
        this.categorieEveneiment = categorieEveneiment;
    }

    @Override
    public String toString() {
        return "Eveniment{" +
                "denumireEveniment='" + denumireEveniment + '\'' +
                ", dataEveniment=" + dataEveniment +
                ", descriereEveniment='" + descriereEveniment + '\'' +
                ", categorieEveneiment='" + categorieEveneiment + '\'' +
                '}';
    }
}
