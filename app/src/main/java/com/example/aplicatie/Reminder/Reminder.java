package com.example.aplicatie.Reminder;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable {
    private String mesaj;
    private Date dataReminder;
    private int id;

    public Reminder() {
    }

    public Reminder(String mesaj, Date dataReminder, int id) {
        this.mesaj = mesaj;
        this.dataReminder = dataReminder;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public Date getDataReminder() {
        return dataReminder;
    }

    public void setDataReminder(Date dataReminder) {
        this.dataReminder = dataReminder;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "mesaj='" + mesaj + '\'' +
                ", dataReminder=" + dataReminder +
                ",id=" + id +
                '}';
    }
}
