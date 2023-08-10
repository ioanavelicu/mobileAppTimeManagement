package com.example.aplicatie.Task;

import com.example.aplicatie.Reminder.Reminder;
import com.example.aplicatie.Sarcina.Sarcina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Task implements Serializable {

    private ArrayList<Sarcina> listaSarcini;
    private String denumireTask;
    private Date deadline;
    private String gradImportanta;
    private String categorie;
    private Reminder reminder;
    private boolean isGata;

    public Task() {
    }

    public Task(String denumireTask, ArrayList<Sarcina> listaSarcini, Date deadline, String gradImportanta, String categorie, Reminder reminder) {
        this.denumireTask = denumireTask;
        this.listaSarcini = listaSarcini;
        this.deadline = deadline;
        this.gradImportanta = gradImportanta;
        this.categorie = categorie;
        this.reminder = reminder;
        this.isGata = false;
    }

    public boolean isGata() {
        return isGata;
    }

    public void setGata(boolean gata) {
        isGata = gata;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public String getDenumireTask() {
        return denumireTask;
    }

    public void setDenumireTask(String denumireTask) {
        this.denumireTask = denumireTask;
    }

    public ArrayList<Sarcina> getListaSarcini() {
        return listaSarcini;
    }

    public void setListaSarcini(ArrayList<Sarcina> listaSarcini) {
        this.listaSarcini = listaSarcini;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getGradImportanta() {
        return gradImportanta;
    }

    public void setGradImportanta(String gradImportanta) {
        this.gradImportanta = gradImportanta;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Task{" +
                "denumire task=" + denumireTask +
                "listaSarcini=" + listaSarcini +
                ", deadline=" + deadline +
                ", gradImportanta='" + gradImportanta + '\'' +
                ", categorie='" + categorie + '\'' +
                ", reminder=" + reminder +
                '}';
    }
}
