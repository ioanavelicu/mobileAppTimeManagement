package com.example.aplicatie.Utilizator;

import com.example.aplicatie.Task.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Utilizator {
    private String nume;
    private String username;
    private String email;
    private String parola;
    private String parolaJurnal;
    private String cotinutJurnal;
    private String pozaProfil;
    private List<String> listaPrieteni;
    private List<String> listaCereriDePrietenieTrimise;
//    private List<String> listaNotitePartajate;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference utilizatori = db.collection("Utilizatori");
    private DocumentReference docUtilizatorCurent = utilizatori.document(FirebaseAuth.getInstance().getUid());

    public Utilizator() {
    }

    public Utilizator(String nume, String username, String email, String parola) {
        this.nume = nume;
        this.username = username;
        this.email = email;
        this.parola = parola;
        this.parolaJurnal = "";
        this.cotinutJurnal = "";
        this.pozaProfil = "https://firebasestorage.googleapis.com/v0/b/login-licenta-908c5.appspot.com/o/imaginiProfil%2Fdefault_profil_user.jpg?alt=media&token=98ea3fdb-f6c4-46ac-8d2a-eb2a694f64c9";
        this.listaPrieteni = new ArrayList<>();
        this.listaCereriDePrietenieTrimise = new ArrayList<>();
    }

    public Utilizator(String username) {
        this.username = username;
    }

    public Utilizator(String username, String pozaProfil) {
        this.username = username;
        this.pozaProfil = pozaProfil;
    }

    public void trimiteCererePrietenie(String username) {
        utilizatori.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot utilizatorCautat : queryDocumentSnapshots.getDocuments()) {
                            if(utilizatorCautat != null) {
//                                docUtilizatorCurent.update("listaCereriDePrietenieTrimise",
//                                        FieldValue.arrayUnion(utilizatorCautat.getId()));
                                docUtilizatorCurent.update("listaCereriDePrietenieTrimise",
                                        FieldValue.arrayUnion(utilizatorCautat.toObject(Utilizator.class).getUsername()));
                            }
                        }
                    }
                });
    }

    public String getPozaProfil() {
        return pozaProfil;
    }

    public void setPozaProfil(String pozaProfil) {
        this.pozaProfil = pozaProfil;
    }

    public List<String> getListaPrieteni() {
        return listaPrieteni;
    }

    public void setListaPrieteni(List<String> listaPrieteni) {
        this.listaPrieteni = listaPrieteni;
    }

    public List<String> getListaCereriDePrietenieTrimise() {
        return listaCereriDePrietenieTrimise;
    }

    public void setListaCereriDePrietenieTrimise(List<String> listaCereriDePrietenieTrimise) {
        this.listaCereriDePrietenieTrimise = listaCereriDePrietenieTrimise;
    }

    public String getCotinutJurnal() {
        return cotinutJurnal;
    }

    public void setCotinutJurnal(String cotinutJurnal) {
        this.cotinutJurnal = cotinutJurnal;
    }

    public String getParolaJurnal() {
        return parolaJurnal;
    }

    public void setParolaJurnal(String parolaJurnal) {
        this.parolaJurnal = parolaJurnal;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
//
//    public ArrayList<Task> getListaTaskuri() {
//        return listaTaskuri;
//    }
//
//    public void setListaTaskuri(ArrayList<Task> listaTaskuri) {
//        this.listaTaskuri = listaTaskuri;
//    }


    @Override
    public String toString() {
        return "Utilizator{" +
                "nume='" + nume + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", parola='" + parola + '\'' +
                ", parolaJurnal='" + parolaJurnal + '\'' +
                '}';
    }
}
